package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

import com.evtnet.evtnetback.Entities.RolUsuario;
import com.evtnet.evtnetback.Entities.RolPermiso;


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

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fecha_hora_alta;

    // 1 rol -> 0..n rol_permiso
    @OneToMany(mappedBy = "rol")
    private List<RolPermiso> rol_permisos;

    // 1 rol -> 0..n rol_usuario
    @OneToMany(mappedBy = "rol")
    private List<RolUsuario> rol_usuarios;
}
