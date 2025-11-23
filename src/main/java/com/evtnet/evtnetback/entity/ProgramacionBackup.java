package com.evtnet.evtnetback.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "programacion_backup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramacionBackup extends com.evtnet.evtnetback.entity.Base {

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDateTime fechaDesde;

    @Column(name = "meses", nullable = false)
    private int meses;       // puede ser 0

    @Column(name = "dias", nullable = false)
    private int dias;        // no más de 30

    @Column(name = "horas", nullable = false)
    private int horas;       // no más de 23

    @Column(name = "copias_incrementales", nullable = false)
    private int copiasIncrementales;

    @Column(name = "copias_conservar", nullable = false)
    private int copiasAConservar;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;
}