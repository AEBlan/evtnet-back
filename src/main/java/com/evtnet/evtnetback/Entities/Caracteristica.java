package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "caracteristica",
    uniqueConstraints = {
        // opcional: evita dos características con mismo nombre en un mismo espacio
        @UniqueConstraint(name = "uk_caracteristica_nombre_por_espacio", columnNames = {"espacio_id", "nombre"})
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caracteristica extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;


    // muchas características -> un espacio (FK vive aquí)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "icono_caracteristica_id") // FK en BD (snake_case)
    private IconoCaracteristica iconoCaracteristica;
}
