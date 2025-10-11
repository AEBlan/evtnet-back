package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rol_permiso")
@NoArgsConstructor @AllArgsConstructor @Builder
public class RolPermiso extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // n..1 -> rol
    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // n..1 -> permiso
    @ManyToOne(optional = false)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
}
