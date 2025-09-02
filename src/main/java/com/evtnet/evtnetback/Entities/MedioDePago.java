package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "medio_de_pago")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedioDePago extends Base {

    @Column(name = "icono")
    private String icono;     // URL o nombre del ícono (png/svg), opcional

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;
}
