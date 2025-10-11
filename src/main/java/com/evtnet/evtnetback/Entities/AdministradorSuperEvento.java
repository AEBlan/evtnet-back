package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "administrador_super_evento"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorSuperEvento extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // muchos administradores -> un super evento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "super_evento_id", nullable = false)
    private SuperEvento superEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_administrador_super_evento_id", nullable = false)
    private TipoAdministradorSuperEvento tipoAdministradorSuperEvento;
}
