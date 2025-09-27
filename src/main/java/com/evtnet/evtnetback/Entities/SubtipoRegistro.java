package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subtipo_registro")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubtipoRegistro extends Base {

    @Column(name = "nombre")
    private String nombre;

    @ManyToMany(mappedBy = "subtipos")
    private List<Registro> registros;
}
