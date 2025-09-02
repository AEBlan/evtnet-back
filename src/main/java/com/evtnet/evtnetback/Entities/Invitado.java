package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.*;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "invitado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitado extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "dni", nullable = false, length = 32)
    private String dni;

    // cada invitado pertenece a UNA inscripción (FK no nula)
    @ManyToOne(optional = false)
    @JoinColumn(name = "inscripcion_id", nullable = false)
    private Inscripcion inscripcion;
}
