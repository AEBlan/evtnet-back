package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

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
