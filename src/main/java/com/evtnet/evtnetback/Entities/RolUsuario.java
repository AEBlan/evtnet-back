import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "RolUsuario")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolUsuario extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 