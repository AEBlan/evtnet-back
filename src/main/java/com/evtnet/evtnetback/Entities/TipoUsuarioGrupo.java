package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_usuario_grupo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoUsuarioGrupo extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 tipo_usuario_grupo -> 0..n usuario_grupo
    @OneToMany(mappedBy = "tipoUsuarioGrupo", fetch = FetchType.EAGER)
    private List<UsuarioGrupo> usuarioGrupos;
}
