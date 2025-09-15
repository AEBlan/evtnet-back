package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "administrador_super_evento",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_admin_superevento_organizador",
            columnNames = {"super_evento_id", "organizador_id"}
        )
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorSuperEvento extends Base {

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    // muchos administradores -> un super evento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "super_evento_id", nullable = false)
    private SuperEvento superEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
