package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_administrador_espacio")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAdministradorEspacio extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "tipoAdministradorEspacio", fetch = FetchType.LAZY)
    private List<AdministradorEspacio> administradorEspacios;

}