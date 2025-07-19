import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ImagenEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenEspacio extends Base {

    @Column(name = "imagen")
    private String imagen;
    
    @Column(name = "orden")
    private Integer orden;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
    
    @ManyToOne
    @JoinColumn(name = "solicitud_espacio_publico_id")
    private SolicitudEspacioPublico solicitudEspacioPublico;
} 