package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "registro")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registro extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "nombre_formateado")
    private String nombreFormateado;

    @ManyToMany
    @JoinTable(
        name = "registro_tipo_registro",
        joinColumns = @JoinColumn(name = "registro_id"),
        inverseJoinColumns = @JoinColumn(name = "tipo_registro_id")
    )
    private List<TipoRegistro> tipos;

    @ManyToMany
    @JoinTable(
        name = "registro_subtipo_registro",
        joinColumns = @JoinColumn(name = "registro_id"),
        inverseJoinColumns = @JoinColumn(name = "subtipo_registro_id")
    )
    private List<SubtipoRegistro> subtipos;
}
