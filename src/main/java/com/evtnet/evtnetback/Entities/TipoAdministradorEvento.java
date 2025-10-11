package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Queue;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tipo_administrador_evento")
@NoArgsConstructor @AllArgsConstructor @Builder
public class TipoAdministradorEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "tipoAdministradorEvento", fetch = FetchType.LAZY)
    private List<AdministradorEvento> administradoresEvento;

}