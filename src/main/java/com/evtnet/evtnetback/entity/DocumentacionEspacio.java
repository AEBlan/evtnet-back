package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "documentacion_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentacionEspacio extends Base {

    @Column(name = "documentacion", nullable = false)
    private String documentacion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

}