package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_excepcion_horario_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoExcepcionHorarioEspacio extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // 1 tipo -> 0..n excepciones
    @OneToMany(mappedBy = "tipoExcepcionHorarioEspacio", fetch = FetchType.EAGER)
    private List<ExcepcionHorarioEspacio> excepcionHorarioEspacios;
}
