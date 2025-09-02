package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "comprobante_pago",
    indexes = {
        @Index(name = "ix_cp_inscripcion", columnList = "inscripcion_id"),
        @Index(name = "ix_cp_evento", columnList = "evento_id"),
        @Index(name = "ix_cp_paga", columnList = "paga_id"),
        @Index(name = "ix_cp_cobro", columnList = "cobro_id"),
        @Index(name = "ix_cp_medio_pago", columnList = "medio_de_pago_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobantePago extends Base {

    @Column(name = "numero")
    private String numero;

    @Column(name = "concepto")
    private String concepto;

    @Column(name = "fecha_hora_emision")
    private LocalDateTime fechaHoraEmision;

    @Column(name = "monto_total_bruto")
    private BigDecimal montoTotalBruto;

    @Column(name = "forma_de_pago")
    private String formaDePago;

    @Column(name = "archivo")
    private String archivo;

    @Column(name = "comision")
    private BigDecimal comision;

    // --- Relaciones ---

    // Muchos comprobantes -> una inscripción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;

    // Muchos comprobantes -> un evento (útil para comprobantes generales del evento/organización)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    // Quien PAGA (usuario emisor del dinero)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paga_id")
    private Usuario paga;

    // Quien COBRA (usuario receptor del dinero)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cobro_id")
    private Usuario cobro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medio_de_pago_id")
    private MedioDePago medioDePago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comision_por_inscripcion_id")
    private ComisionPorInscripcion comisionPorInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comision_por_organizacion_id")
    private ComisionPorOrganizacion comisionPorOrganizacion;
}
