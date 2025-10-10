package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

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
}
