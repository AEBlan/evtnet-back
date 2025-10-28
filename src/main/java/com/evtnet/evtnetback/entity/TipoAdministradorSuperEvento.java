package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_administrador_superevento")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAdministradorSuperEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "tipoAdministradorSuperEvento", fetch = FetchType.LAZY)
    private List<AdministradorSuperEvento> administradorSuperEventos;

}