package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "transicion_estado_espacio")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TransicionEstadoEspacio extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_origen_id", nullable = false)
    private EstadoEspacio estadoOrigen;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_destino_id", nullable = false)
    private EstadoEspacio estadoDestino;

}