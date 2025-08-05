package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraInicio")
    private LocalDateTime fechaHoraInicio;
    
    @Column(name = "fechaHoraFin")
    private LocalDateTime fechaHoraFin;
    
    @Column(name = "direccionUbicacion")
    private String direccionUbicacion;
    
    @Column(name = "longitudUbicacion")
    private BigDecimal longitudUbicacion;
    
    @Column(name = "latitudUbicacion")
    private BigDecimal latitudUbicacion;
    
    @Column(name = "precioInscripcion")
    private BigDecimal precioInscripcion;
    
    @Column(name = "cantidadMaximaInvitados")
    private Integer cantidadMaximaInvitados;
    
    @Column(name = "cantidadMaximaParticipantes")
    private Integer cantidadMaximaParticipantes;
    
    @Column(name = "precioOrganizacion")
    private BigDecimal precioOrganizacion;
    
    @ManyToOne
    @JoinColumn(name = "super_evento_id")
    private SuperEvento superEvento;
    
    @ManyToMany
    @JoinTable(
        name = "evento_disciplina",
        joinColumns = @JoinColumn(name = "evento_id"),
        inverseJoinColumns = @JoinColumn(name = "disciplina_evento_id")
    )
    private List<DisciplinaEvento> disciplinasEvento;
    
    @OneToMany(mappedBy = "evento")
    private List<Inscripcion> inscripciones;
    
    @ManyToOne
    @JoinColumn(name = "tipo_inscripcion_evento_id")
    private TipoInscripcionEvento tipoInscripcionEvento;
    
    @ManyToOne
    @JoinColumn(name = "modo_evento_id")
    private ModoEvento modoEvento;
    
    @ManyToOne
    @JoinColumn(name = "disciplina_evento_id")
    private DisciplinaEvento disciplinaEvento;
    
    @ManyToOne
    @JoinColumn(name = "administrador_evento_id")
    private AdministradorEvento administradorEvento;
    
    @OneToMany(mappedBy = "evento")
    private List<EventoModoEvento> eventosModoEvento;
    
    @OneToMany(mappedBy = "evento")
    private List<PorcentajeReintegroCancelacionInscripcion> porcentajesReintegroCancelacion;
    
    @OneToMany(mappedBy = "evento")
    private List<DenunciaEvento> denunciasEvento;
} 