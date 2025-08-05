package com.evtnet.evtnetback.Entities;
import com.evtnet.evtnetback.Entities.Base;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "UsuarioGrupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioGrupo extends Base {

    @Column(name = "fechaHoraAlta")
    private LocalDateTime fechaHoraAlta;
    
    @Column(name = "fechaHoraBaja")
    private LocalDateTime fechaHoraBaja;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "usuario1_id")
    private Usuario usuario1;
    
    @ManyToOne
    @JoinColumn(name = "usuario2_id")
    private Usuario usuario2;
    
    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;
    
    @ManyToOne
    @JoinColumn(name = "tipo_usuario_grupo_id")
    private TipoUsuarioGrupo tipoUsuarioGrupo;
} 