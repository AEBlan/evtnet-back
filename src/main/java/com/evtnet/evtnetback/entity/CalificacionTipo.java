package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "calificacion_tipo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionTipo extends Base {

    @Column(name = "nombre")
    private String nombre;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @OneToMany(mappedBy = "calificacionTipo")
    private List<Calificacion> calificaciones;
}
