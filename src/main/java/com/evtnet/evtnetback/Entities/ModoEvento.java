package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "modo_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModoEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    // 1 modo_evento -> 0..n evento_modo_evento
    @OneToMany(mappedBy = "modoEvento")        
    private List<EventoModoEvento> eventoModoEventos;
}
