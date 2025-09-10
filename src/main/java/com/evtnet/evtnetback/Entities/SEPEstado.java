package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private LocalDateTime fechaHoraHasta;

    @Column(name = "fecha_hora_desde", nullable = false)
    private LocalDateTime fechaHoraDesde;

    // n..1: muchas filas -> un EstadoSEP
    @ManyToOne(optional = false)
    @JoinColumn(name = "estado_sep_id", nullable = false)
    private EstadoSEP estadoSEP;

    // n..1: muchas filas -> un Usuario (quien registró el estado)
    @ManyToOne(optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;

    // n..1: muchas filas -> una SolicitudEspacioPublico (objeto al que pertenece el estado)
    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitud_espacio_publico_id", nullable = false)
    private SolicitudEspacioPublico solicitudEspacioPublico;
}
