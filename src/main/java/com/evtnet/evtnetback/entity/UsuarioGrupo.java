package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "usuario_grupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioGrupo extends Base {

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // n..1: muchos registros -> un usuario
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // n..1: muchos registros -> un grupo
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    // n..1: muchos registros -> un tipo de usuario dentro del grupo (Miembro/Administrador)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_usuario_grupo_id", nullable = false)
    private TipoUsuarioGrupo tipoUsuarioGrupo;
}
