package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "grupo", indexes = {
    @Index(name = "ix_grupo_chat", columnList = "chat_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    // Relaciones
    @OneToMany(mappedBy = "grupo")
    private List<UsuarioGrupo> usuariosGrupo;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_id", nullable = false, unique = true)
    private Chat chat;


} 