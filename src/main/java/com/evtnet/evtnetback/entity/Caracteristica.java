package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "caracteristica"
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caracteristica extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    // muchas características -> un espacio (FK vive aquí)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subespacio_id", nullable = false)
    private SubEspacio subEspacio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "icono_caracteristica_id") // FK en BD (snake_case)
    private IconoCaracteristica iconoCaracteristica;
}
