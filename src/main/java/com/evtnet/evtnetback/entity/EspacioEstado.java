package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "espacio_estado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspacioEstado extends Base {

    @Column(name = "descripcion")
    private String descripcion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_espacio_id", nullable = false)
    private EstadoEspacio estadoEspacio;
}