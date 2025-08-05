package com.evtnet.evtnetback.Entities;

import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ComisionPorInscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComisionPorInscripcion extends Base {

    @Column(name = "fechaHasta")
    private LocalDateTime fechaHasta;
    
    @Column(name = "fechaDesde")
    private LocalDateTime fechaDesde;
    
    @Column(name = "montoLimite")
    private BigDecimal montoLimite;
    
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
} 