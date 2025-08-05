package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "RolPermiso")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermiso extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "rolId")
    private Rol rol;
    
    @ManyToOne
    @JoinColumn(name = "permisoId")
    private Permiso permiso;
} 