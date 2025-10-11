package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

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

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 tipo -> 1..n motivos
    @OneToMany(mappedBy = "tipoCalificacion", fetch = FetchType.EAGER)
    private List<MotivoCalificacion> motivoCalificaciones;
}
