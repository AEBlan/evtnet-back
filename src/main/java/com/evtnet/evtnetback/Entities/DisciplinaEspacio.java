package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DisciplinaEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaEspacio extends Base {

    // Relaciones
    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
} 