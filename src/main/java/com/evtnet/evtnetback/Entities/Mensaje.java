import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Mensaje")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje extends Base {

    @Column(name = "texto")
    private String texto;
    
    @Column(name = "fechaHora")
    private LocalDateTime fechaHora;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
} 