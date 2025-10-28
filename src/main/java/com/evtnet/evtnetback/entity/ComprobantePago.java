package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comprobante_pago")
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

    @Column(name = "archivo", nullable = false)
    private String archivo;

    // --- Relaciones ---

    // Muchos comprobantes -> una inscripción
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @OneToMany(mappedBy = "comprobantePago", fetch = FetchType.LAZY)
    private List<ItemComprobantePago> items;

}
