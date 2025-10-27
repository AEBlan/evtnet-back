package com.evtnet.evtnetback.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "horario_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioEspacio extends Base {

    @Column(name = "dia_semana")
    private String diaSemana;

    @Column(name = "hora_desde")
    private LocalTime horaDesde;

    @Column(name = "hora_hasta")
    private LocalTime horaHasta;

    @Column(name = "precio_organizacion")
    private BigDecimal precioOrganizacion;

    @Column(name ="adicional_por_inscripcion")
    private BigDecimal adicionalPorInscripcion;

    // relación con configuración 
    @ManyToOne
    @JoinColumn(name = "configuracion_horario_espacio_id")
    private ConfiguracionHorarioEspacio configuracionHorarioEspacio;
}