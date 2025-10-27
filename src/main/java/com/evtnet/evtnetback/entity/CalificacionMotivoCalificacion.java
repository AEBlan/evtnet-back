package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "calificacion_motivo_calificacion"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionMotivoCalificacion extends Base {

    // Muchas filas para una misma Calificacion
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calificacion_id", nullable = false)
    private Calificacion calificacion;

    // Muchas filas para un mismo motivo
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "motivo_calificacion_id", nullable = false)
    private MotivoCalificacion motivoCalificacion;
}
