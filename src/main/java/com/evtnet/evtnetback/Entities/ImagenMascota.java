package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ImagenMascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenMascota extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "imagen")
    private String imagen;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "instancia_mascota_id")
    private InstanciaMascota instanciaMascota;
} 