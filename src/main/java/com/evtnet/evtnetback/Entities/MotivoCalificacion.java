package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "motivo_calificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotivoCalificacion extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    // n..1: muchos motivos pertenecen a un tipo
    @ManyToOne(optional = false)
    @JoinColumn(name = "tipo_calificacion_id", nullable = false)
    private TipoCalificacion tipoCalificacion;

    // 1 motivo -> 0..n filas puente
    @OneToMany(mappedBy = "motivoCalificacion")
    private List<CalificacionMotivoCalificacion> calificacionMotivoCalificaciones;
}
