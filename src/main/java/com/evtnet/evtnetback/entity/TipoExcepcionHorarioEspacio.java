package com.evtnet.evtnetback.entity;

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

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 tipo -> 0..n excepciones
    @OneToMany(mappedBy = "tipoExcepcionHorarioEspacio", fetch = FetchType.EAGER)
    private List<ExcepcionHorarioEspacio> excepcionHorarioEspacios;
}
