import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Grupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    // Relaciones
    @OneToMany(mappedBy = "grupo")
    private List<UsuarioGrupo> usuariosGrupo;
    
    @OneToOne(mappedBy = "grupo")
    private Chat chat;
} 