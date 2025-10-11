package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rol")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Rol extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 rol -> 0..n rol_permiso
    @OneToMany(mappedBy = "rol")
    private List<RolPermiso> rolPermisos;

    // 1 rol -> 0..n rol_usuario
    @OneToMany(mappedBy = "rol")
    private List<RolUsuario> rolUsuarios;
}
