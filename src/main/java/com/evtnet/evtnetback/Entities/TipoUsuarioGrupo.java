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

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fecha_hora_alta;

    // 1 tipo_usuario_grupo -> 0..n usuario_grupo
    @OneToMany(mappedBy = "tipo_usuario_grupo", fetch = FetchType.EAGER)
    private List<UsuarioGrupo> usuario_grupos;
}
