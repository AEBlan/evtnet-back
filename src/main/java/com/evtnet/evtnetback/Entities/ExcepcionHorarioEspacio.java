package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "excepcion_horario_espacio"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcepcionHorarioEspacio extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // N -> 1 ConfiguracionHorarioEspacio
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_horario_espacio_id", nullable = false)
    private ConfiguracionHorarioEspacio configuracionHorarioEspacio;

    // N -> 1 TipoExcepcionHorarioEspacio  (CORREGIDO)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_excepcion_horario_espacio_id", nullable = false)
    private TipoExcepcionHorarioEspacio tipoExcepcionHorarioEspacio;
}
