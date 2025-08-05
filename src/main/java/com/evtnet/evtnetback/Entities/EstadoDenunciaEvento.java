package com.evtnet.evtnetback.Entities;

import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EstadoDenunciaEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoDenunciaEvento extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "estadoDenunciaEvento")
    private List<DenunciaEventoEstado> denunciasEventoEstado;
} 