package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString(exclude = {"disciplinasEvento", "inscripciones", "eventosModoEvento", "porcentajesReintegroCancelacion", "denunciasEvento"})
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_inicio") // antes: fechaHoraInicio
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin") // antes: fechaHoraFin
    private LocalDateTime fechaHoraFin;

    @Column(name = "direccion_ubicacion")
    private String direccionUbicacion;

    @Column(name = "longitud_ubicacion")
    private BigDecimal longitudUbicacion;

    @Column(name = "latitud_ubicacion")
    private BigDecimal latitudUbicacion;

    @Column(name = "precio_inscripcion")
    private BigDecimal precioInscripcion;

    @Column(name = "cantidad_maxima_invitados")
    private Integer cantidadMaximaInvitados;

    @Column(name = "cantidad_maxima_participantes")
    private Integer cantidadMaximaParticipantes;

    @Column(name = "precio_organizacion")
    private BigDecimal precioOrganizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_evento_id")
    private SuperEvento superEvento;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DisciplinaEvento> disciplinasEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_inscripcion_evento_id")
    private TipoInscripcionEvento tipoInscripcionEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modo_evento_id")
    private ModoEvento modoEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdministradorEvento> administradoresEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<EventoModoEvento> eventosModoEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<PorcentajeReintegroCancelacionInscripcion> porcentajesReintegroCancelacion;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<DenunciaEvento> denunciasEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;

    @OneToOne(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Chat chat;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<ComprobantePago> comprobantesPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id")
    private Usuario organizador;
}