package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrador_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorEvento extends Base {

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    // 🔹 MUCHOS administradores → UN mismo usuario (quién administra)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_id", nullable = false)
    private Usuario responsable;

    // 🔹 MUCHOS administradores → UN mismo evento (qué evento administra)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;


}