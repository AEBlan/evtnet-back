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
@Table(name = "ComprobantePago")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobantePago extends Base {

    @Column(name = "numero")
    private String numero;
    
    @Column(name = "concepto")
    private String concepto;
    
    @Column(name = "fechaHoraEmision")
    private LocalDateTime fechaHoraEmision;
    
    @Column(name = "montoTotalBruto")
    private BigDecimal montoTotalBruto;
    
    @Column(name = "formaDePago")
    private String formaDePago;
    
    @Column(name = "archivo")
    private String archivo;
    
    @Column(name = "comision")
    private BigDecimal comision;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;
    
    @ManyToOne
    @JoinColumn(name = "medio_de_pago_id")
    private MedioDePago medioDePago;
    
    @ManyToOne
    @JoinColumn(name = "comision_por_inscripcion_id")
    private ComisionPorInscripcion comisionPorInscripcion;
    
    @ManyToOne
    @JoinColumn(name = "comision_por_organizacion_id")
    private ComisionPorOrganizacion comisionPorOrganizacion;
} 