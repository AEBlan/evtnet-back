package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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