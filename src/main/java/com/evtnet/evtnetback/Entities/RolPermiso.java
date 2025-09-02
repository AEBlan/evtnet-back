package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rol_permiso")
@NoArgsConstructor @AllArgsConstructor @Builder
public class RolPermiso extends Base {

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fecha_hora_alta;

    // n..1 -> rol
    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // n..1 -> permiso
    @ManyToOne(optional = false)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
}
