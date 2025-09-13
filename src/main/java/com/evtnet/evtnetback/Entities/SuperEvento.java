package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "super_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // 1 super_evento -> 0..n eventos
    @OneToMany(mappedBy = "superEvento", fetch = FetchType.EAGER)
    private List<Evento> eventos;

    // 1 super_evento -> 0..n administradores de super evento
    @OneToMany(mappedBy = "superEvento", fetch = FetchType.EAGER)
    private List<AdministradorSuperEvento> administradorSuperEventos;

    // 1 super_evento -> 1 chat (FK está en Chat)
    @OneToOne(mappedBy = "superEvento", fetch = FetchType.EAGER)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
