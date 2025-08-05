package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "TipoCalificacion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCalificacion extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "imagen")
    private String imagen;
    
    // Relaciones
    @OneToMany(mappedBy = "tipoCalificacion")
    private List<MotivoCalificacion> motivosCalificacion;
} 