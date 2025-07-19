import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "UsuarioInstanciaMascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioInstanciaMascota extends Base {

    @Column(name = "fechaHora")
    private LocalDateTime fechaHora;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "instancia_mascota_id")
    private InstanciaMascota instanciaMascota;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 