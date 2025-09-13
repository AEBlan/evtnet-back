package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_inscripcion_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoInscripcionEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // 1 tipo -> 0..n eventos
    @OneToMany(mappedBy = "tipoInscripcionEvento", fetch = FetchType.EAGER)
    private List<Evento> eventos;
}
