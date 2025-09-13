package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_calificacion")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoCalificacion extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "imagen")
    private String imagen;

    // 1 tipo -> 1..n motivos
    @OneToMany(mappedBy = "tipoCalificacion", fetch = FetchType.EAGER)
    private List<MotivoCalificacion> motivoCalificaciones;
}
