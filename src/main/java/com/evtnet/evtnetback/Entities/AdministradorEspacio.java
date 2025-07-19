import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "AdministradorEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorEspacio extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToOne
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;
    
    @OneToMany(mappedBy = "administradorEspacio")
    private List<Espacio> espacios;
} 