package com.evtnet.evtnetback.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EstadoSEP")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSEP extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "descripcion")
    private String descripcion;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "estadoSEP")
    private List<SEPEstado> sepEstados;

    @OneToMany(mappedBy = "estadoOrigen")
    private List<TransicionEstadoSEP> transicionesDesde;

    @OneToMany(mappedBy = "estadoDestino")
    private List<TransicionEstadoSEP> transicionesHacia;
} 