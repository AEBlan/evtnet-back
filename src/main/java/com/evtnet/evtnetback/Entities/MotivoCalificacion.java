package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "MotivoCalificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotivoCalificacion extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "tipo_calificacion_id")
    private TipoCalificacion tipoCalificacion;
    
    @OneToMany(mappedBy = "motivoCalificacion")
    private List<CalificacionMotivoCalificacion> calificacionesMotivoCalificacion;
} 