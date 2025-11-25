package com.evtnet.evtnetback.dto.comprobante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOComprobante {

    private Long numero;
    private String concepto;
    private Long fechaHoraEmision; // epoch millis
    private String formaDePago;

    private Persona pago;
    private Persona cobro;

    private Double montoTotalBruto;
    private Double comision;
    private Boolean evtnetPagaComision;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Persona {
        private String nombre;
        private String apellido;
        private String dni;
        private String cbu;
    }
}
