package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

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
    @OneToMany(mappedBy = "super_evento", fetch = FetchType.EAGER)
    private List<Evento> eventos;

    // 1 super_evento -> 0..n administradores de super evento
    @OneToMany(mappedBy = "super_evento", fetch = FetchType.EAGER)
    private List<AdministradorSuperEvento> administrador_super_eventos;

    // 1 super_evento -> 1 chat (FK está en Chat)
    @OneToOne(mappedBy = "super_evento", fetch = FetchType.EAGER)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Usuario organizador;
}
