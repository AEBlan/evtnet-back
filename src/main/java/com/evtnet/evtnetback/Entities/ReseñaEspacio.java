package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ReseñaEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReseñaEspacio extends Base {

    @Column(name = "titulo")
    private String titulo;
    
    @Column(name = "comentario")
    private String comentario;
    
    @Column(name = "puntaje")
    private Integer puntaje;
    
    @Column(name = "fechaHora")
    private LocalDateTime fechaHora;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "administrador_espacio_id")
    private AdministradorEspacio administradorEspacio;
} 