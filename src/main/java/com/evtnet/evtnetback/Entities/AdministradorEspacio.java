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
@Table(
    name = "administrador_espacio",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_admin_espacio_propietario",
            columnNames = {"espacio_id", "propietario_id"}
        )
    }
)
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
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;

    // muchos administradores -> un espacio
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;
}