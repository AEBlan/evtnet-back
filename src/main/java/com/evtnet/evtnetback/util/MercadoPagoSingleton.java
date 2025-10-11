package com.evtnet.evtnetback.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPago;
import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;

@Component
public class MercadoPagoSingleton {

    private final String baseUrl;

    private final boolean debug = true;

    public MercadoPagoSingleton(@Value("${app.frontend.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public DTOPreferenciaPago createPreference(
        String concepto,
        BigDecimal montoBruto,
        BigDecimal comision,
        Usuario destinatario,
        String url
    ) throws Exception {

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

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success(url)
            .pending(url)
            .failure(url)
            .build();

        PreferencePaymentMethodsRequest paymentMethod = PreferencePaymentMethodsRequest.builder()
            .excludedPaymentTypes(List.of(PreferencePaymentTypeRequest.builder().id("ticket").build()))
            .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .marketplaceFee(comision.multiply(new BigDecimal(10.0)))
            .backUrls(backUrls)
            .binaryMode(true)
            .paymentMethods(paymentMethod)
            .items(items)

            .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return DTOPreferenciaPago.builder()
            .concepto(concepto)
            .montoBruto(montoBruto)
            .comision(comision)
            .preference_id(preference.getId())
            .public_key("APP_USR-14995dd8-9cbc-4d03-b581-71dacead45ba")
            .completada(false)
            .build();
    }



    public void verifyPayments(List<DTOPago> pagos) throws Exception {
        if (debug) return;

        for (DTOPago pago : pagos) {
            //TO-DO: Verificar que todos los pagos se hayan realizado correctamente
        }
    }

    public void refundPayment(String paymentId) throws Exception {
        if (debug) return;
        
        //TO-DO: Reembolsar pagos
    }
}
