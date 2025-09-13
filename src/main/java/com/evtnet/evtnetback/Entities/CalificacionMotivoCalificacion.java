package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "calificacion_motivo_calificacion",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_calif_motivo_por_calificacion",
            columnNames = {"calificacion_id", "motivo_calificacion_id"}
        )
    }
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
