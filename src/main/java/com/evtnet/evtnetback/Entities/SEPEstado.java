package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sep_estado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SEPEstado extends Base {

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_hasta")
    private LocalDateTime fecha_hora_hasta;

    @Column(name = "fecha_hora_desde", nullable = false)
    private LocalDateTime fecha_hora_desde;

    // n..1: muchas filas -> un EstadoSEP
    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_sep_id", nullable = false)
    private EstadoSEP estado_sep;

    // n..1: muchas filas -> un Usuario (quien registró el estado)
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // n..1: muchas filas -> una SolicitudEspacioPublico (objeto al que pertenece el estado)
    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitud_espacio_publico_id", nullable = false)
    private SolicitudEspacioPublico solicitud_espacio_publico;
}
