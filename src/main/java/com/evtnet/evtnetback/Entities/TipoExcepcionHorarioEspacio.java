import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "TipoExcepcionHorarioEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoExcepcionHorarioEspacio extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "excepcion_horario_espacio_id")
    private ExcepcionHorarioEspacio excepcionHorarioEspacio;
} 