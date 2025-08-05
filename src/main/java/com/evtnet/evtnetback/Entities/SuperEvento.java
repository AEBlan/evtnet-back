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
@Table(name = "SuperEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperEvento extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    @OneToMany(mappedBy = "superEvento")
    private List<Evento> eventos;
    
    @ManyToOne
    @JoinColumn(name = "administrador_super_evento_id")
    private AdministradorSuperEvento administradorSuperEvento;
} 