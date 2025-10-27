package com.evtnet.evtnetback.Backup.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "programacion_backup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramacionBackup extends com.evtnet.evtnetback.Entities.Base {

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;
    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    @Column(name = "intervalo_horas", nullable = false)
    private  LocalDateTime intervaloHoras;

    @Column(name = "fecha_desde", nullable = false)
    private  LocalDateTime fechaDesde;

    @Column(name = "copias_incrementales", nullable = false)
    private  BigDecimal copiasincrementales;

    @Column(name = "copias_conservar", nullable = false)
    private  BigDecimal copiasAconservar;
}