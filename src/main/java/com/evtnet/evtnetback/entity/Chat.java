package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true, exclude = {"evento", "superEvento", "espacio", "usuario1", "usuario2"})
@Entity
@Table(name = "chat",
       indexes = {
         @Index(name = "ix_chat_evento", columnList = "evento_id"),
         @Index(name = "ix_chat_superevento", columnList = "super_evento_id"),
         @Index(name = "ix_chat_espacio", columnList = "espacio_id"),
         @Index(name = "ix_chat_usuario1", columnList = "usuario1_id"),
         @Index(name = "ix_chat_usuario2", columnList = "usuario2_id")
       },
       uniqueConstraints = {
        // único chat directo por par (u1,u2)
        @UniqueConstraint(name = "uk_chat_directo", columnNames = {"usuario1_id","usuario2_id"})
      }
       )
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat extends Base {

  public enum Tipo { DIRECTO, EVENTO, SUPEREVENTO, ESPACIO }

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false)
  private Tipo tipo;

  @Column(name = "fecha_hora_alta")
  private LocalDateTime fechaHoraAlta;

  @Column(name = "fecha_hora_baja")
  private LocalDateTime fechaHoraBaja;

  // ---- Chat directo (entre 2 usuarios) ----
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario1_id")
  private Usuario usuario1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario2_id")
  private Usuario usuario2;

  // ---- Chat de sala (un único ámbito) ----
  //@ManyToOne(fetch = FetchType.LAZY)
  //@JoinColumn(name = "evento_id", unique = true)
  //private Evento evento;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "evento_id", unique = true)
  private Evento evento;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "super_evento_id", unique = true)
  private SuperEvento superEvento;

  // ---- N chats pueden pertenecer a un mismo espacio
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "espacio_id")
  private Espacio espacio;

  // ---- Hijos ----
  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Mensaje> mensajes;

  @OneToOne(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Grupo grupo;
}
