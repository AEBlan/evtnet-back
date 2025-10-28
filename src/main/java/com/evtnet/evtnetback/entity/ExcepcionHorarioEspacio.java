package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    private LocalDateTime fechaHoraDesde;
    private LocalDateTime fechaHoraHasta;

    // N -> 1 ConfiguracionHorarioEspacio
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "configuracion_horario_espacio_id", nullable = false)
    private ConfiguracionHorarioEspacio configuracionHorarioEspacio;

    // N -> 1 TipoExcepcionHorarioEspacio  (CORREGIDO)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_excepcion_horario_espacio_id", nullable = false)
    private TipoExcepcionHorarioEspacio tipoExcepcionHorarioEspacio;
}
