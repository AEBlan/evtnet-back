package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.SubEspacio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
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

    @Column(name = "precio_inscripcion")
    private BigDecimal precioInscripcion;

    @Column(name = "cantidad_maxima_invitados")
    private Integer cantidadMaximaInvitados;

    @Column(name = "cantidad_maxima_participantes")
    private Integer cantidadMaximaParticipantes;

    @Column(name = "precio_organizacion")
    private BigDecimal precioOrganizacion;

    @Column(name = "adicional_por_inscripcion")
    private BigDecimal adicionalPorInscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_evento_id")
    private SuperEvento superEvento;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DisciplinaEvento> disciplinasEvento;

    //@OneToMany(mappedBy = "evento", fetch = FetchType.LAZY
    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "tipo_inscripcion_evento_id")
    //private TipoInscripcionEvento tipoInscripcionEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdministradorEvento> administradoresEvento;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<PorcentajeReintegroCancelacionInscripcion> porcentajesReintegroCancelacion;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<DenunciaEvento> denunciasEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subespacio_id", nullable = false)
    private SubEspacio subEspacio;

    //@OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    //private List<Chat> chats;

    @OneToOne(mappedBy = "evento", fetch = FetchType.LAZY, orphanRemoval = true)
    private Chat chat;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<ComprobantePago> comprobantesPago;

    @OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
    private List<EventoEstado> eventosEstado;


    public Usuario getOrganizador() throws Exception {
        for (AdministradorEvento a : administradoresEvento) {
            if (a.getTipoAdministradorEvento().getNombre().equalsIgnoreCase("Organizador")) {
                return a.getUsuario();
            };
        }
        throw new Exception("El evento no tiene un organizador");
    }
    
}