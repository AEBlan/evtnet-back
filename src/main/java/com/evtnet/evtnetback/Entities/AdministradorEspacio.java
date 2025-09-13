package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "administrador_espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorEspacio extends Base {

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    // muchos administradores -> un usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // muchos administradores -> un espacio
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;
}