package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "configuracion_horario_espacio",
    indexes = {
        @Index(name = "ix_conf_he_espacio", columnList = "espacio_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionHorarioEspacio extends Base {

    @Column(name = "dias_hacia_adelante")
    private Integer diasHaciaAdelante;

    @Column(name = "fecha_desde")
    private LocalDateTime fechaDesde;

    @Column(name = "fecha_hasta")
    private LocalDateTime fechaHasta;

    // N -> 1 Espacio (dueño FK: esta tabla)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    // 1 -> N HorarioEspacio (inverso en HorarioEspacio)
    @OneToMany(mappedBy = "configuracionHorarioEspacio",
               cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HorarioEspacio> horariosEspacio;

    // 1 -> N ExcepcionHorarioEspacio (inverso en ExcepcionHorarioEspacio)
    @OneToMany(mappedBy = "configuracionHorarioEspacio",
               cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ExcepcionHorarioEspacio> excepcionesHorarioEspacio;
}
