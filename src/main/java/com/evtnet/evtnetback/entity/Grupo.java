package com.evtnet.evtnetback.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "grupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo extends Base {            

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;
    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    @OneToMany(mappedBy = "grupo")
    private List<UsuarioGrupo> usuariosGrupo;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false, unique = true)
    private Chat chat;


} 