package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estado_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // 1 -> N EventoEstado (inverso)
    @OneToMany(mappedBy = "estadoEvento")
    private List<EventoEstado> eventoEstados;

    // 1 -> N TransicionEstadoEvento (origen)
    @OneToMany(mappedBy = "estadoOrigen")
    private List<TransicionEstadoEvento> transicionesOrigen;

    // 1 -> N TransicionEstadoEvento (destino)
    @OneToMany(mappedBy = "estadoDestino")
    private List<TransicionEstadoEvento> transicionesDestino;
}
