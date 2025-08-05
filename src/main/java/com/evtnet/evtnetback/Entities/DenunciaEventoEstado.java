package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DenunciaEventoEstado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaEventoEstado extends Base {

    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraDesde")
    private LocalDateTime fechaHoraDesde;
    
    @Column(name = "fechaHoraHasta")
    private LocalDateTime fechaHoraHasta;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "estado_denuncia_evento_id")
    private EstadoDenunciaEvento estadoDenunciaEvento;
    
    @ManyToOne
    @JoinColumn(name = "denuncia_evento_id")
    private DenunciaEvento denunciaEvento;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 