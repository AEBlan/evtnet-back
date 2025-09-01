package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.*;
import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion extends Base {

    // --- Atributos (respeta DER) ---
    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fecha_hora_alta;

    // NO agrego fecha_hora_baja: ya heredás fecha_baja en Base

    @Column(name = "precio_inscripcion", precision = 15, scale = 2)
    private BigDecimal precio_inscripcion;

    @Column(name = "permitir_devolucion_completa", nullable = false)
    private Boolean permitir_devolucion_completa;

    // --- Relaciones ---
    // muchas inscripciones pertenecen a un usuario
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // muchas inscripciones pertenecen a un evento
    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // una inscripción puede tener 0..n invitados
    @OneToMany(mappedBy = "inscripcion")
    private List<Invitado> invitados;

    // una inscripción puede tener 0..n comprobantes de pago
    @OneToMany(mappedBy = "inscripcion")
    private List<ComprobantePago> comprobante_pagos;
}
