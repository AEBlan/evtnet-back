package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rol_usuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolUsuario extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // muchas asignaciones -> un rol
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // muchas asignaciones -> un usuario
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    void prePersist() {
        if (fechaHoraAlta == null) fechaHoraAlta = LocalDateTime.now();
    }
}
