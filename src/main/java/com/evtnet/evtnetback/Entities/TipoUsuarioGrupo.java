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
@Table(name = "TipoUsuarioGrupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuarioGrupo extends Base {

    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @OneToMany(mappedBy = "tipoUsuarioGrupo")
    private List<UsuarioGrupo> usuariosGrupo;
} 