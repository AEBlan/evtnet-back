package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "CalificacionMotivoCalificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionMotivoCalificacion extends Base {

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "tipo_calificacion_id")
    private TipoCalificacion tipoCalificacion;
    
    @ManyToOne
    @JoinColumn(name = "motivo_calificacion_id")
    private MotivoCalificacion motivoCalificacion;
    
    @OneToOne
    @JoinColumn(name = "calificacion_id")
    private Calificacion calificacion;
} 