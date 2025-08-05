package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Invitado")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitado extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "apellido")
    private String apellido;
    
    @Column(name = "dni")
    private String dni;
    
    // Relaciones
    @OneToMany(mappedBy = "invitado")
    private List<Inscripcion> inscripciones;
} 