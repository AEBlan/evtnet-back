package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEspacio extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // 1 tipo_espacio -> 0..n espacios
    @OneToMany(mappedBy = "tipoEspacio", fetch = FetchType.EAGER)
    private List<Espacio> espacios;
}
