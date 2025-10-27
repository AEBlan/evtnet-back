package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

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
