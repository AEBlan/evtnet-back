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
@Table(name = "InstanciaMascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanciaMascota extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "pageRegex")
    private String pageRegex;
    
    @Column(name = "events")
    private String events;
    
    @Column(name = "selector")
    private String selector;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "instanciaMascota")
    private List<UsuarioInstanciaMascota> usuariosInstanciaMascota;
    
    @OneToMany(mappedBy = "instanciaMascota")
    private List<InstanciaMascotaSecuencia> secuencias;
    
    @OneToMany(mappedBy = "instanciaMascota")
    private List<ImagenMascota> imagenesMascota;
} 