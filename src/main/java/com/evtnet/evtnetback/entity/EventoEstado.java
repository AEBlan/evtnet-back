package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evento_estado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoEstado extends Base {

    @Column(name = "descripcion")
    private String descripcion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_evento_id")
    private EstadoEvento estadoEvento;
}