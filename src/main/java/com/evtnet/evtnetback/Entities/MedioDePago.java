import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "MedioDePago")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedioDePago extends Base {

    @Column(name = "icono")
    private String icono;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
} 