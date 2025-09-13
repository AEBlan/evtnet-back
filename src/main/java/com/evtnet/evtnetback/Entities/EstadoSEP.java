package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EstadoSEP")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSEP extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    // Relaciones
    @OneToMany(mappedBy = "estadoSEP")
    private List<SEPEstado> sepEstados;
} 