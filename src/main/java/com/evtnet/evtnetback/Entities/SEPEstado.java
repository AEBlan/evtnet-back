package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SEPEstado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SEPEstado extends Base {

    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraHasta")
    private LocalDateTime fechaHoraHasta;
    
    @Column(name = "fechaHoraDesde")
    private LocalDateTime fechaHoraDesde;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "estado_sep_id")
    private EstadoSEP estadoSEP;
    
    @ManyToOne
    @JoinColumn(name = "solicitud_espacio_publico_id")
    private SolicitudEspacioPublico solicitudEspacioPublico;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 