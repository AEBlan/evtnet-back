package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "denuncia_evento_estado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaEventoEstado extends Base {

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "fecha_hora_desde")
    private LocalDateTime fechaHoraDesde;

    @Column(name = "fecha_hora_hasta")
    private LocalDateTime fechaHoraHasta;

    // N -> 1 EstadoDenunciaEvento (Ingresado / Finalizado, etc.)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_denuncia_evento_id", nullable = false)
    private EstadoDenunciaEvento estadoDenunciaEvento;

    // N -> 1 DenunciaEvento (a cuál denuncia pertenece este estado)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "denuncia_evento_id", nullable = false)
    private DenunciaEvento denunciaEvento;

    // N -> 1 Usuario (quien cambió el estado)  — en el diagrama aparece como “responsable”
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;
}
