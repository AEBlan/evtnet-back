import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Disciplina")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disciplina extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "disciplina")
    private List<DisciplinaEspacio> disciplinasEspacio;
    
    @OneToMany(mappedBy = "disciplina")
    private List<DisciplinaEvento> disciplinasEvento;
} 