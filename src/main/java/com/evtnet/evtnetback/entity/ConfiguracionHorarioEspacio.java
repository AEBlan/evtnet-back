package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "configuracion_horario_espacio"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionHorarioEspacio extends Base {

    @Column(name = "dias_anteelacion", nullable = false)
    private Integer diasAntelacion;

    @Column(name = "fecha_desde", nullable = false)
    private LocalDateTime fechaDesde;

    @Column(name = "fecha_hasta", nullable = false)
    private LocalDateTime fechaHasta;

    // N -> 1 Espacio (dueño FK: esta tabla)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subespacio_id", nullable = false)
    private SubEspacio subEspacio;

    // 1 -> N HorarioEspacio (inverso en HorarioEspacio)
    @OneToMany(mappedBy = "configuracionHorarioEspacio",
               cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HorarioEspacio> horariosEspacio;

    // 1 -> N ExcepcionHorarioEspacio (inverso en ExcepcionHorarioEspacio)
    @OneToMany(mappedBy = "configuracionHorarioEspacio",
               cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExcepcionHorarioEspacio> excepcionesHorarioEspacio;
}
