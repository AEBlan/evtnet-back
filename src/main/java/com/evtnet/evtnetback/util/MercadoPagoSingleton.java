package com.evtnet.evtnetback.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

import com.evtnet.evtnetback.config.jackson.LocalDateTimeFlexDeserializer;
import com.evtnet.evtnetback.entity.ComprobantePago;
import com.evtnet.evtnetback.entity.ItemComprobantePago;
import com.evtnet.evtnetback.repository.ComprobantePagoRepository;
import com.evtnet.evtnetback.repository.ItemComprobantePagoRepository;
import com.evtnet.evtnetback.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentRefundClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentFeeDetail;
import com.mercadopago.resources.payment.PaymentRefund;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.evtnet.evtnetback.entity.Usuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPago;
import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.MercadoPagoConfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MercadoPagoSingleton {

    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String marketPlaceToken;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UsuarioRepository usuarioRepository;
    private final ComprobantePagoRepository comprobantePagoRepository;
    private final ItemComprobantePagoRepository itemComprobantePagoRepository;

    private final boolean debugPayments;
    private final boolean debugRefunds;

    public MercadoPagoSingleton(
            @Value("${app.frontend.baseUrl}") String baseUrl,
            @Value("${mercadopago.client.id}") String clientId,
            @Value("${mercadopago.client.secret}") String clientSecret,
            @Value("${mercadopago.redirect.uri}") String redirectUri,
            @Value("${mercadopago.marketplace.token}") String marketPlaceToken,
            UsuarioRepository usuarioRepository,
            ComprobantePagoRepository comprobantePagoRepository,
            ItemComprobantePagoRepository itemComprobantePagoRepository,

            @Value("${mercadopago.debug.payments}") boolean debugPayments,
            @Value("${mercadopago.debug.refunds}") boolean debugRefunds
    ) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.marketPlaceToken = marketPlaceToken;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.usuarioRepository = usuarioRepository;
        this.comprobantePagoRepository = comprobantePagoRepository;
        this.itemComprobantePagoRepository = itemComprobantePagoRepository;
        this.debugPayments = debugPayments;
        this.debugRefunds = debugRefunds;
    }


    public boolean checkUsuarioAutorizado(Usuario usuario) {
        if (usuario.getMercadoPagoUserId() == null
                || usuario.getMercadoPagoPublicKey() == null
                || usuario.getMercadoPagoPublicKey().isEmpty()
                || usuario.getMercadoPagoAccessToken() == null
                || usuario.getMercadoPagoAccessToken().isEmpty()
                || usuario.getMercadoPagoRefreshToken() == null
                || usuario.getMercadoPagoRefreshToken().isEmpty()
        ) {
            return false;
        }
        return true;
    }

    // OAuth Methods

    /**
     * Generates the authorization URL to redirect sellers to Mercado Pago OAuth
     * @param state Optional state parameter for CSRF protection
     * @return Authorization URL
     */
    public String getAuthorizationUrl(String state) {
        StringBuilder url = new StringBuilder("https://auth.mercadopago.com/authorization");
        url.append("?response_type=code");
        url.append("&client_id=").append(clientId);
        url.append("&redirect_uri=").append(redirectUri);

        if (state != null && !state.isEmpty()) {
            url.append("&state=").append(state);
        }

        return url.toString();
    }

    /**
     * Exchanges authorization code for access token
     * Based on official Mercado Pago documentation
     * @param authorizationCode The code received after user authorization
     * @return OAuthCredentials object containing access_token, refresh_token, and user_id
     * @throws Exception if the exchange fails
     */
    public OAuthCredentials exchangeCodeForToken(String authorizationCode, String state) throws Exception {
        // Using the Java SDK's OauthClient as shown in documentation
        try {
            //OauthClient client = new OauthClient();

            // Note: The SDK's createCredential method is limited
            // For production, you may need to use RestTemplate for full control
            //client.createCredential(authorizationCode, getAuthorizationUrl(state));

            // The SDK method doesn't return the credentials directly
            // So we need to make a manual REST call for complete information
            return exchangeCodeViaRest(authorizationCode);
        } catch (MPApiException e){
            throw new Exception("Error exchanging authorization code: " + e.getApiResponse().getContent(), e);
        } catch (Exception e) {
            throw new Exception("Error exchanging authorization code: " + e.getMessage(), e);
        }
    }

    /**
     * Manual REST implementation for exchanging code (more reliable)
     * Based on official API documentation
     */
    private OAuthCredentials exchangeCodeViaRest(String authorizationCode) throws Exception {
        String url = "https://api.mercadopago.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OAuthTokenRequest requestBody = new OAuthTokenRequest();
        requestBody.clientId = this.clientId;
        requestBody.clientSecret = this.clientSecret;
        requestBody.grantType = "authorization_code";
        requestBody.code = authorizationCode;
        requestBody.redirectUri = this.redirectUri;
        requestBody.testToken = "true";

        HttpEntity<OAuthTokenRequest> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OAuthCredentials> response = restTemplate.postForEntity(
                    url,
                    request,
                    OAuthCredentials.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new Exception("Failed to exchange code. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new Exception("Error calling OAuth token endpoint: " + e.getMessage(), e);
        }
    }

    /**
     * Refreshes an expired access token using refresh token
     * Based on official Mercado Pago documentation
     * @param refreshToken The refresh token to exchange
     * @return New OAuthCredentials with updated access_token and refresh_token
     * @throws Exception if refresh fails
     */
    public OAuthCredentials refreshAccessToken(String refreshToken) throws Exception {
        String url = "https://api.mercadopago.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        OAuthTokenRequest requestBody = new OAuthTokenRequest();
        requestBody.clientId = this.clientId;
        requestBody.clientSecret = this.clientSecret;
        requestBody.grantType = "refresh_token";
        requestBody.refreshToken = refreshToken;

        HttpEntity<OAuthTokenRequest> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<OAuthCredentials> response = restTemplate.postForEntity(
                    url,
                    request,
                    OAuthCredentials.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new Exception("Failed to refresh token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new Exception("Error refreshing access token: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a preference for a seller using their OAuth access token
     * For marketplace model with split payments
     */
    public DTOPreferenciaPago createPreference(
            String concepto,
            BigDecimal montoBruto,
            BigDecimal comision,
            Usuario destinatario,
            String url
    ) throws Exception {

        // Get seller's credentials from Usuario
        String sellerAccessToken = destinatario.getMercadoPagoAccessToken();
        String sellerPublicKey = destinatario.getMercadoPagoPublicKey();

        if (sellerAccessToken == null || sellerAccessToken.isEmpty()) {
            throw new Exception("El destinatario no ha vinculado su cuenta de Mercado Pago.");
        }

        // Configure SDK to use seller's access token
        MercadoPagoConfig.setAccessToken(sellerAccessToken);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("1234")
                .title(concepto)
                .quantity(1)
                .currencyId("ARS")
                .unitPrice(montoBruto.multiply(comision.add(new BigDecimal(1.0))))
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        url = baseUrl + url;

        url = baseUrl + "/Pago";

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(url)
                .pending(url)
                .failure(url)
                .build();

        PreferencePaymentMethodsRequest paymentMethod = PreferencePaymentMethodsRequest.builder()
                .excludedPaymentTypes(List.of(PreferencePaymentTypeRequest.builder().id("ticket").build()))
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .marketplaceFee(comision.multiply(montoBruto))
                .backUrls(backUrls)
                .binaryMode(true)
                .paymentMethods(paymentMethod)
                .items(items)
                .externalReference(concepto)
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);
        String pref_id = preference.getId();

        return DTOPreferenciaPago.builder()
                .concepto(concepto)
                .montoBruto(montoBruto)
                .comision(comision)
                .preference_id(pref_id)
                .public_key(sellerPublicKey)
                .completada(false)
                .build();
    }

    public List<ComprobantePago> verifyPayments(List<DTOPago> pagos) throws Exception {
        if (debugPayments) {
            // Return mock comprobantes in debug mode
            String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión vencida"));
            Usuario usuarioLogueado = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("No se encontró al usuario que pagó"));

            List<ComprobantePago> comprobantes = new ArrayList<>();
            for (DTOPago pago : pagos) {
                ComprobantePago comprobante = comprobantePagoRepository.save(ComprobantePago.builder()
                        .numero(pago.getPaymentId())
                        .concepto(pago.getExternal_reference())
                        .fechaHoraEmision(LocalDateTime.now())
                        .archivo("/mock/path/receipt.pdf")
                        .build());
                comprobantes.add(comprobante);
            }
            return comprobantes;
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión vencida"));
        Usuario usuarioLogueado = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("No se encontró al usuario que pagó"));

        List<ComprobantePago> comprobantes = new ArrayList<>();

        for (DTOPago pago : pagos) {
            MercadoPagoConfig.setAccessToken(pago.getDestinatario().getMercadoPagoAccessToken());

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(Long.valueOf(pago.getPaymentId()));



            // Download PDF receipt
            String pdfPath = null;
            try {
                pdfPath = generateReceiptPdf(payment, usuarioLogueado, pago.getDestinatario());
                //pdfPath = downloadReceipt(pago.getPaymentId());
            } catch (IOException ignored) {

            }

            // Create and save comprobante
            ComprobantePago comprobante = comprobantePagoRepository.save(ComprobantePago.builder()
                    .numero(pago.getPaymentId())
                    .concepto(pago.getExternal_reference())
                    .fechaHoraEmision(payment.getDateApproved().toLocalDateTime())
                    .archivo(pdfPath)
                    .build());

            BigDecimal feeAmount = payment.getFeeDetails().stream().filter(f -> f.getType().equalsIgnoreCase("application_fee")).findFirst().orElse(new PaymentFeeDetail()).getAmount();
            if (feeAmount == null) feeAmount = BigDecimal.ZERO;

            BigDecimal grossAmount = payment.getTransactionAmount().subtract(feeAmount);

            // Create payment item
            itemComprobantePagoRepository.save(ItemComprobantePago.builder()
                    .detalle(pago.getExternal_reference())
                    .montoUnitario(grossAmount)
                    .cantidad(1)
                    .pago(usuarioLogueado)
                    .cobro(pago.getDestinatario())
                    .comprobantePago(comprobante)
                    .build());

            // Create commission item
            itemComprobantePagoRepository.save(ItemComprobantePago.builder()
                    .detalle("Comisión de evtnet")
                    .montoUnitario(feeAmount)
                    .cantidad(1)
                    .pago(usuarioLogueado)
                    .cobro(null)
                    .comprobantePago(comprobante)
                    .build());

            comprobantes.add(comprobante);
        }

        return comprobantes;
    }

    public String generateReceiptPdf(Payment payment, Usuario buyer, Usuario seller) throws Exception {
        // === 1. Output path ===
        String comprobantesDir = "/app/storage/comprobantes";
        Files.createDirectories(Paths.get(comprobantesDir));

        String fileName = "receipt_" + payment.getId() + ".pdf";
        Path filePath = Paths.get(comprobantesDir, fileName);

        // === 2. Small A5 page ===
        Document document = new Document(PageSize.A5);
        PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
        document.open();

        // === 3. Logo ===
        File file = new File(getClass().getResource("/default.png").getFile());
        Path path = file.toPath();
        Image logo = Image.getInstance(path.toAbsolutePath().toString());
        logo.scaleToFit(70, 70);
        logo.setAlignment(Element.ALIGN_CENTER);
        document.add(logo);

        document.add(new Paragraph("\n"));

        // === 4. Title ===
        Paragraph title = new Paragraph("RECIBO DE PAGO");
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // === 5. Subtitle (evtnet) ===
        Paragraph evtnet = new Paragraph("emitido por evtnet");
        evtnet.setAlignment(Element.ALIGN_CENTER);
        document.add(evtnet);

        document.add(new Paragraph("\n\n"));

        // === 6. Core receipt text ===
        BigDecimal gross = payment.getTransactionAmount();
        BigDecimal net = payment.getTransactionDetails().getNetReceivedAmount();

        Paragraph mainText = new Paragraph(
                "Yo, " + seller.getNombre() + " " + seller.getApellido() + " (DNI " + seller.getDni() + ", @" + seller.getUsername() + ") " +
                        ", recibí de " + buyer.getNombre() + " " + buyer.getApellido() + " (DNI " + buyer.getDni() + ", @" + buyer.getUsername() + ") " +
                        " la suma de $" + net + "."
        );
        mainText.setAlignment(Element.ALIGN_LEFT);
        document.add(mainText);

        document.add(new Paragraph("\n"));

        // === 7. Fees ===
        BigDecimal mpFee = BigDecimal.ZERO;
        BigDecimal evtFee = BigDecimal.ZERO;

        if (payment.getFeeDetails() != null) {
            for (PaymentFeeDetail fee : payment.getFeeDetails()) {
                if ("mercadopago_fee".equalsIgnoreCase(fee.getType())) {
                    mpFee = mpFee.add(fee.getAmount());
                } else {
                    evtFee = evtFee.add(fee.getAmount());
                }
            }
        }

        document.add(new Paragraph("Monto bruto abonado: $" + gross));
        document.add(new Paragraph("Comisión Mercado Pago: $" + mpFee));
        document.add(new Paragraph("Comisión evtnet: $" + evtFee));
        document.add(new Paragraph("\n"));

        // === 8. Payment extra info ===
        document.add(new Paragraph("ID de pago MP: " + payment.getId()));
        if (payment.getDescription() != null) {
            document.add(new Paragraph("Concepto: " + payment.getDescription()));
        }

        document.add(new Paragraph("Fecha: " + payment.getDateApproved()));

        document.add(new Paragraph("\n\nGracias por utilizar evtnet."));

        // === 9. Close ===
        document.close();

        return fileName;
    }


    public void refundPayment(ComprobantePago comprobante) throws Exception {
        refundPayment(comprobante, 100);
    }

    public void refundPayment(ComprobantePago comprobante, int porcentaje) throws Exception {
        if (debugRefunds) return;

        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        String paymentId = comprobante.getNumero();

        if (paymentId == null || paymentId.isEmpty()) {
            throw new Exception("El comprobante no tiene un payment_id asociado");
        }

        // Get seller's access token from the first item (cobro)
        if (comprobante.getItems() == null || comprobante.getItems().isEmpty()) {
            throw new Exception("El comprobante no tiene items");
        }

        String sellerAccessToken = comprobante.getItems().stream().filter(i -> i.getCobro() != null).max(Comparator.comparing(ItemComprobantePago::getDetalle)).orElseThrow(() -> new Exception("No se encontró el destinatario de la transferencia original")).getCobro().getMercadoPagoAccessToken();

        if (sellerAccessToken == null || sellerAccessToken.isEmpty()) {
            throw new Exception("No se puede reembolsar: el destinatario no tiene token de Mercado Pago");
        }

        MercadoPagoConfig.setAccessToken(sellerAccessToken);
        PaymentRefundClient client = new PaymentRefundClient();
        BigDecimal monto = comprobante.getItems().stream()
                .map(item -> item.getMontoUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        try {
            PaymentRefund refund = client.refund(Long.valueOf(paymentId), monto);
        } catch (Exception e) {
            throw new Exception("Error reembolsando pago " + paymentId + ": " + e.getMessage(), e);
        }
    }

    public void refundIncompletePayments(List<DTOPago> pagos) throws Exception {
        if (debugRefunds) return;

        for (DTOPago pago : pagos) {
            if (pago.getPaymentId() == null || pago.getPaymentId().isEmpty()) {
                continue;
            }

            if (pago.getDestinatario() == null) {
                throw new Exception("No se pudo identificar al destinatario del pago");
            }
            String sellerAccessToken = pago.getDestinatario().getMercadoPagoAccessToken();
            if (sellerAccessToken == null || sellerAccessToken.isEmpty()) {
                throw new Exception("No se puede reembolsar: el destinatario no tiene token de Mercado Pago");
            }


            MercadoPagoConfig.setAccessToken(sellerAccessToken);
            PaymentRefundClient client = new PaymentRefundClient();

            try {
                PaymentRefund refund = client.refund(Long.valueOf(pago.getPaymentId()));
            } catch (Exception e) {
                throw new Exception("Error reembolsando pago no completo " + pago.getPaymentId()  + ": " + e.getMessage(), e);
            }
        }
    }

// DTOs for Payment API

    public static class PaymentResponse {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("status")
        public String status;

        @JsonProperty("status_detail")
        public String statusDetail;

        @JsonProperty("transaction_amount")
        public BigDecimal transactionAmount;

        @JsonProperty("marketplace_fee")
        public BigDecimal marketplaceFee;

        @JsonProperty("date_approved")
        @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
        public LocalDateTime dateApproved;

        @JsonProperty("metadata")
        public java.util.Map<String, String> metadata;
    }

    public static class RefundRequest {
        @JsonProperty("amount")
        public BigDecimal amount;
    }

    public static class RefundResponse {
        @JsonProperty("id")
        public Long id;

        @JsonProperty("payment_id")
        public Long paymentId;

        @JsonProperty("amount")
        public BigDecimal amount;

        @JsonProperty("status")
        public String status;
    }

    // DTOs for OAuth

    public static class OAuthTokenRequest {
        @JsonProperty("client_id")
        public String clientId;

        @JsonProperty("client_secret")
        public String clientSecret;

        @JsonProperty("grant_type")
        public String grantType;

        @JsonProperty("code")
        public String code;

        @JsonProperty("redirect_uri")
        public String redirectUri;

        @JsonProperty("refresh_token")
        public String refreshToken;

        @JsonProperty("test_tojen")
        public String testToken;
    }

    public static class OAuthCredentials {
        @JsonProperty("access_token")
        public String accessToken;

        @JsonProperty("token_type")
        public String tokenType;

        @JsonProperty("expires_in")
        public Long expiresIn;

        @JsonProperty("scope")
        public String scope;

        @JsonProperty("user_id")
        public Long userId;

        @JsonProperty("refresh_token")
        public String refreshToken;

        @JsonProperty("public_key")
        public String publicKey;

        @JsonProperty("live_mode")
        public Boolean liveMode;
    }
}