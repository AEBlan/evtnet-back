package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estado_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEspacio extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "estadoEspacio")
    private List<EspacioEstado> espacioEstado;

    @OneToMany(mappedBy = "estadoOrigen")
    private List<TransicionEstadoEspacio> transicionesOrigen;

    @OneToMany(mappedBy = "estadoDestino")
    private List<TransicionEstadoEspacio> transicionesDestino;
}
