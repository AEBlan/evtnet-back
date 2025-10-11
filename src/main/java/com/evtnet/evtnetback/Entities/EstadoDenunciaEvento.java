package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estado_denuncia_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoDenunciaEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 -> N DenunciaEventoEstado (inverso)
    @OneToMany(mappedBy = "estadoDenunciaEvento")
    private List<DenunciaEventoEstado> denunciasEventoEstado;

    // 1 -> N TransicionEstadoDenuncia (origen)
    @OneToMany(mappedBy = "estadoOrigen")
    private List<TransicionEstadoDenuncia> transicionesOrigen;

    // 1 -> N TransicionEstadoDenuncia (destino)
    @OneToMany(mappedBy = "estadoDestino")
    private List<TransicionEstadoDenuncia> transicionesDestino;
}
