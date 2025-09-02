package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "excepcion_horario_espacio",
    indexes = {
        @Index(name = "ix_excep_conf", columnList = "configuracion_horario_espacio_id"),
        @Index(name = "ix_excep_tipo", columnList = "tipo_excepcion_horario_espacio_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcepcionHorarioEspacio extends Base {

    @Column(name = "fecha_hora_desde")
    private LocalDateTime fechaHoraDesde;

    @Column(name = "fecha_hora_hasta")
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
