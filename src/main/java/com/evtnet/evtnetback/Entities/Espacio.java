package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Espacio extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    @Column(name = "direccionUbicacion")
    private String direccionUbicacion;
    
    @Column(name = "latitudUbicacion")
    private BigDecimal latitudUbicacion;
    
    @Column(name = "longitudUbicacion")
    private BigDecimal longitudUbicacion;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "administrador_espacio_id")
    private AdministradorEspacio administradorEspacio;
    
    @OneToMany(mappedBy = "espacio")
    private List<ImagenEspacio> imagenesEspacio;
    
    @OneToMany(mappedBy = "espacio")
    private List<ConfiguracionHorarioEspacio> configuracionesHorario;
    
    @OneToMany(mappedBy = "espacio")
    private List<ReseñaEspacio> reseñasEspacio;
    
    @OneToMany(mappedBy = "espacio")
    private List<DisciplinaEspacio> disciplinasEspacio;
    
    @ManyToOne
    @JoinColumn(name = "tipo_espacio_id")
    private TipoEspacio tipoEspacio;
    
    @ManyToMany
    @JoinTable(
        name = "espacio_caracteristica",
        joinColumns = @JoinColumn(name = "espacio_id"),
        inverseJoinColumns = @JoinColumn(name = "caracteristica_id")
    )
    private List<Caracteristica> caracteristicas;
} 