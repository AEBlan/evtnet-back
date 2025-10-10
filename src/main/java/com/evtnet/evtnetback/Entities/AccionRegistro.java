package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "accion_registro")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccionRegistro extends Base {

    @Column(name = "nombre")
    private String nombre;

}
