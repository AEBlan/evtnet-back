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
@Table(name = "Inscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    @Column(name = "precioInscripcion")
    private BigDecimal precioInscripcion;
    
    @Column(name = "permitirDevolucionCompleta")
    private Boolean permitirDevolucionCompleta;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;
    
    @ManyToOne
    @JoinColumn(name = "invitado_id")
    private Invitado invitado;
    
    @OneToMany(mappedBy = "inscripcion")
    private List<DenunciaEvento> denunciasEvento;
    
    @ManyToOne
    @JoinColumn(name = "administrador_evento_id")
    private AdministradorEvento administradorEvento;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "tipo_inscripcion_evento_id")
    private TipoInscripcionEvento tipoInscripcionEvento;
    
    @ManyToOne
    @JoinColumn(name = "porcentaje_reintegro_cancelacion_inscripcion_id")
    private PorcentajeReintegroCancelacionInscripcion porcentajeReintegroCancelacionInscripcion;
    
    @OneToMany(mappedBy = "inscripcion")
    private List<ComprobantePago> comprobantesPago;
} 