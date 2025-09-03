package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "denuncia_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DenunciaEvento extends Base {

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    // -------- Relaciones --------

    // N -> 1 Evento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    // (opcional) la denuncia puede estar asociada a una inscripción concreta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscripcion_id")
    private Inscripcion inscripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Usuario denunciante;

    // 1 -> N Estados de la denuncia
    @OneToMany(mappedBy = "denunciaEvento")
    private List<DenunciaEventoEstado> estados;
}
