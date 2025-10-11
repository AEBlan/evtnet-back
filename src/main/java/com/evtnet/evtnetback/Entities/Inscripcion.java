package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // --- Atributos ---
    @Column(name = "precio_inscripcion", precision = 15, scale = 2)
    private BigDecimal precioInscripcion;

    @Column(name = "permitir_devolucion_completa", nullable = false)
    private Boolean permitirDevolucionCompleta;

    // --- Relaciones ---
    // muchas inscripciones pertenecen a un usuario
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // muchas inscripciones pertenecen a un evento
    @ManyToOne(optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // una inscripción puede tener 0..n invitados
    @OneToMany(mappedBy = "inscripcion")
    private List<Invitado> invitados;

    // una inscripción puede tener 0..n comprobantes de pago
    @OneToMany(mappedBy = "inscripcion")
    private List<ComprobantePago> comprobantePagos;
}
