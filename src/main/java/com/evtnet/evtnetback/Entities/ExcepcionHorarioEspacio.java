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
@Table(name = "ExcepcionHorarioEspacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExcepcionHorarioEspacio extends Base {

    @Column(name = "fechaHoraDesde")
    private LocalDateTime fechaHoraDesde;
    
    @Column(name = "fechaHoraHasta")
    private LocalDateTime fechaHoraHasta;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "configuracion_horario_espacio_id")
    private ConfiguracionHorarioEspacio configuracionHorarioEspacio;
    
    @OneToMany(mappedBy = "excepcionHorarioEspacio")
    private List<TipoExcepcionHorarioEspacio> tiposExcepcionHorarioEspacio;
} 