package com.evtnet.evtnetback.Entities;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comision_por_organizacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComisionPorOrganizacion extends Base {

    @Column(name = "fecha_hasta")
    private LocalDateTime fechaHasta;
    
    @Column(name = "fecha_desde")
    private LocalDateTime fechaDesde;
    
    @Column(name = "monto_limite")
    private BigDecimal montoLimite;
    
    @Column(name = "porcentaje")
    private BigDecimal porcentaje;
} 