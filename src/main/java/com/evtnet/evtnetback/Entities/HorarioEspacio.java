import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "HorarioEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioEspacio extends Base {

    @Column(name = "diaSemana")
    private String diaSemana;
    
    @Column(name = "horaDesde")
    private LocalTime horaDesde;
    
    @Column(name = "horaHasta")
    private LocalTime horaHasta;
    
    @Column(name = "precioOrganizacion")
    private BigDecimal precioOrganizacion;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "configuracion_horario_espacio_id")
    private ConfiguracionHorarioEspacio configuracionHorarioEspacio;
} 