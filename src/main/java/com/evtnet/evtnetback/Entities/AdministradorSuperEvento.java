import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "AdministradorSuperEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdministradorSuperEvento extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToOne
    @JoinColumn(name = "organizador_id")
    private Usuario organizador;
    
    @OneToMany(mappedBy = "administradorSuperEvento")
    private List<SuperEvento> superEventos;
} 