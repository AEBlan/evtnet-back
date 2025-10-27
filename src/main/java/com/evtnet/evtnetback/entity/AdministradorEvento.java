package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrador_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorEvento extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // 🔹 MUCHOS administradores → UN mismo evento (qué evento administra)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_administrador_evento_id", nullable = false)
    private TipoAdministradorEvento tipoAdministradorEvento;


}