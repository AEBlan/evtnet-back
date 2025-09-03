package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.Calificacion;

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

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    @OneToMany(mappedBy = "calificacionTipo")
    private List<Calificacion> calificaciones;
}
