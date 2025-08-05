package com.evtnet.evtnetback.Entities;

import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Calificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion extends Base {

    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHora")
    private LocalDateTime fechaHora;
    
    // Relaciones
    @OneToOne(mappedBy = "calificacion")
    private CalificacionMotivoCalificacion calificacionMotivoCalificacion;
    
    @ManyToOne
    @JoinColumn(name = "calificacion_tipo_id")
    private CalificacionTipo calificacionTipo;
    
    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;
    
    @ManyToOne
    @JoinColumn(name = "calificado_id")
    private Usuario calificado;
    
} 