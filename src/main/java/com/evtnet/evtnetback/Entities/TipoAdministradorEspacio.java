package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Queue;

import com.evtnet.evtnetback.Entities.AdministradorEspacio;

import java.time.LocalDateTime;

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