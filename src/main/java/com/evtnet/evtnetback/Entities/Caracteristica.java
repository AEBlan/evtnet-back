package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.evtnet.evtnetback.Entities.Espacio;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "icono_caracteristica_id")
    private IconoCaracteristica icono_caracteristica;
    
    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
} 

    // muchas características -> un espacio (FK vive aquí)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    // 1 característica -> N íconos
    @OneToMany(mappedBy = "caracteristica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IconoCaracteristica> iconosCaracteristica;
}
