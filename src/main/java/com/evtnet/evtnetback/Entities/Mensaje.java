package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "mensaje")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje extends Base {

    @Column(name = "texto", nullable = false, length = 1000)
    private String texto;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // --- Relaciones ---

    // Muchos mensajes -> un chat
    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    // Muchos mensajes -> un usuario (autor)
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
