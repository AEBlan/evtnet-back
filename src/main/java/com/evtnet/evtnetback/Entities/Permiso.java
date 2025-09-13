package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permiso")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Permiso extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // 1 permiso -> 0..n rol_permiso
    @OneToMany(mappedBy = "permiso")
    private List<RolPermiso> rolPermisos;
}
