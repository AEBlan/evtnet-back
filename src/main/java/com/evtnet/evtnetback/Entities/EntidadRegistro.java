package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "entidad_registro")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntidadRegistro extends Base {

    @Column(name = "nombre")
    private String nombre;
}
