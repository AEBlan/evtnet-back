package com.evtnet.evtnetback.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.evtnet.evtnetback.entity.ComprobantePago;
import com.mercadopago.exceptions.MPApiException;
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
import com.mercadopago.client.oauth.OauthClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MercadoPagoSingleton {

    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final boolean debug = false;

    public MercadoPagoSingleton(
            @Value("${app.frontend.baseUrl}") String baseUrl,
            @Value("${mercadopago.client.id}") String clientId,
            @Value("${mercadopago.client.secret}") String clientSecret,
            @Value("${mercadopago.redirect.uri}") String redirectUri
    ) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
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

    public void verifyPayments(List<DTOPago> pagos) throws Exception {
        if (debug) return;

        for (DTOPago pago : pagos) {
            String url = "https://api.mercadopago.com/v1/payments/" + pago.getPaymentId();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + MercadoPagoConfig.getAccessToken());

            HttpEntity<String> request = new HttpEntity<>(headers);

            try {
                ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        PaymentResponse.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {
                    PaymentResponse payment = response.getBody();

                    if (!"approved".equals(payment.status)) {
                        throw new Exception("Pago " + pago.getPaymentId() + " no está aprobado. Estado: " + payment.status);
                    }

                } else {
                    throw new Exception("Error al verificar pago " + pago.getPaymentId() + ". Status: " + response.getStatusCode());
                }

            } catch (Exception e) {
                throw new Exception("Error verificando pago " + pago.getPaymentId() + ": " + e.getMessage(), e);
            }
        }
    }

    public void refundPayment(ComprobantePago comprobante) throws Exception {
        refundPayment(comprobante, 100);
    }

    public void refundPayment(ComprobantePago comprobante, int porcentaje) throws Exception {
        if (debug) return;

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

        String sellerAccessToken = comprobante.getItems().get(0).getCobro().getMercadoPagoAccessToken();

        if (sellerAccessToken == null || sellerAccessToken.isEmpty()) {
            throw new Exception("No se puede reembolsar: el vendedor no tiene token de Mercado Pago");
        }

        String url = "https://api.mercadopago.com/v1/payments/" + paymentId + "/refunds";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + sellerAccessToken);

        RefundRequest refundRequest = new RefundRequest();

        if (porcentaje < 100) {
            // Calculate total amount from items (gross + commission)
            BigDecimal totalAmount = comprobante.getItems().stream()
                    .map(item -> item.getMontoUnitario().multiply(new BigDecimal(item.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            refundRequest.amount = totalAmount.multiply(new BigDecimal(porcentaje)).divide(new BigDecimal(100));
        }
        // If porcentaje == 100, send empty/null amount for full refund

        HttpEntity<RefundRequest> request = new HttpEntity<>(refundRequest, headers);

        try {
            ResponseEntity<RefundResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    RefundResponse.class
            );

            if (response.getStatusCode() != HttpStatus.CREATED &&
                    response.getStatusCode() != HttpStatus.OK) {
                throw new Exception("Error al reembolsar pago " + paymentId + ". Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new Exception("Error reembolsando pago " + paymentId + ": " + e.getMessage(), e);
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