package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "porcentaje_reintegro_cancelacion_inscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorcentajeReintegroCancelacionInscripcion extends Base {

    @Column(name = "minutos_limite", nullable = false)
    private Integer minutos_limite;             // minutos antes del evento

    @Column(name = "porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;              // ej: 100.00, 50.00

    // n..1: muchos porcentajes pertenecen a un evento
    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
}
