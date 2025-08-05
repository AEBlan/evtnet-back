package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Rol")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "rolPermiso")
    private List<RolPermiso> rolesPermiso;
    
    @OneToMany(mappedBy = "rolUsuario")
    private List<RolUsuario> rolesUsuario;
} 