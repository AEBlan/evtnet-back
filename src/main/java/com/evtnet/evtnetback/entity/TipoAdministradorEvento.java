package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_administrador_evento")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAdministradorEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "tipoAdministradorEvento", fetch = FetchType.LAZY)
    private List<AdministradorEvento> administradoresEvento;

}