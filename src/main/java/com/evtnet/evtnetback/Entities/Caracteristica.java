package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.evtnet.evtnetback.Entities.Espacio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Caracteristica")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caracteristica extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "icono_caracteristica_id")
    private IconoCaracteristica icono_caracteristica;
    
    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
} 