package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DenunciaEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaEvento extends Base {

    @Column(name = "titulo")
    private String titulo;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "denunciante_id")
    private Usuario denunciante;
    
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;
    
    @ManyToOne
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;
    
    @OneToMany(mappedBy = "denunciaEvento")
    private List<DenunciaEventoEstado> denunciasEventoEstado;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 