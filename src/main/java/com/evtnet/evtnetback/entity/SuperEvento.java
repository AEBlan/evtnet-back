package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
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

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 super_evento -> 0..n eventos
    @OneToMany(mappedBy = "superEvento", fetch = FetchType.EAGER)
    private List<Evento> eventos;

    // 1 super_evento -> 0..n administradores de super evento
    @OneToMany(mappedBy = "superEvento", fetch = FetchType.EAGER)
    private List<AdministradorSuperEvento> administradorSuperEventos;

    // 1 super_evento -> n chat (FK está en Chat)
    @OneToMany(mappedBy = "superEvento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chat> chats;

}
