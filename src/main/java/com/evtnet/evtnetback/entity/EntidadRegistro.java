package com.evtnet.evtnetback.Entity;

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
