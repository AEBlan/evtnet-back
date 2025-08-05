package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "PorcentajeReintegroCancelacionInscripcion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PorcentajeReintegroCancelacionInscripcion extends Base {

    @Column(name = "minutosLimite")
    private Integer minutosLimite;
    
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;
} 