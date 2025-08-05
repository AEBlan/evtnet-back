package main.java.com.evtnet.evtnetback.Entities;
//import com.evtnet.evtnetback.Entities.Base;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SolicitudEspacioPublico")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEspacioPublico extends Base {

    @Column(name = "nombreEspacio")
    private String nombreEspacio;
    
    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "latitudUbicacion")
    private BigDecimal latitudUbicacion;
    
    @Column(name = "longitudUbicacion")
    private BigDecimal longitudUbicacion;
    
    @Column(name = "direccionUbicacion")
    private String direccionUbicacion;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "justificacion")
    private String justificacion;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;
    
    @OneToMany(mappedBy = "solicitudEspacioPublico")
    private List<SEPEstado> sepEstados;
    
    @OneToMany(mappedBy = "solicitudEspacioPublico")
    private List<Espacio> espacios;
} 