package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "calificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion extends Base {

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    // ---- Relaciones ----

    // Muchas calificaciones para un tipo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calificacion_tipo_id")
    private CalificacionTipo calificacionTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calificado_id", nullable = false)
    private Usuario calificado;

    // 1 Calificacion -> N CalificacionMotivoCalificacion
    @OneToMany(
        mappedBy = "calificacion",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<CalificacionMotivoCalificacion> motivos;
}
