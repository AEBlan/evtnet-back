package com.evtnet.evtnetback.Entities;

import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConfiguracionHorarioEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionHorarioEspacio extends Base {

    @Column(name = "diasHaciaAdelante")
    private Integer diasHaciaAdelante;
    
    @Column(name = "fechaDesde")
    private LocalDateTime fechaDesde;
    
    @Column(name = "fechaHasta")
    private LocalDateTime fechaHasta;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "espacio_id")
    private Espacio espacio;
    
    @OneToMany(mappedBy = "configuracionHorarioEspacio")
    private List<HorarioEspacio> horariosEspacio;
    
    @OneToMany(mappedBy = "configuracionHorarioEspacio")
    private List<ExcepcionHorarioEspacio> excepcionesHorarioEspacio;
} 