package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "usuario_instancia_mascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioInstanciaMascota extends Base {

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    // n..1: muchas filas -> un usuario
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // n..1: muchas filas -> una instancia_mascota
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "instancia_mascota_id", nullable = false)
    private InstanciaMascota instanciaMascota;
}
