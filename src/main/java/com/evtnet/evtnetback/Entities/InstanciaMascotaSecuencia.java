package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "InstanciaMascotaSecuencia")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanciaMascotaSecuencia extends Base {

    @Column(name = "texto")
    private String texto;
    
    @Column(name = "orden")
    private Integer orden;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "instancia_mascota_id")
    private InstanciaMascota instanciaMascota;
} 