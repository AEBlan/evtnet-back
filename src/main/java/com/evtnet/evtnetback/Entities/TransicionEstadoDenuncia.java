package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "transicion_estado_denuncia")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TransicionEstadoDenuncia extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_origen_id", nullable = false)
    private EstadoDenunciaEvento estadoOrigen;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "estado_destino_id", nullable = false)
    private EstadoDenunciaEvento estadoDestino;

}
