package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "denuncia_evento",
       indexes = {
         @Index(name = "ix_denuev_evento", columnList = "evento_id"),
         @Index(name = "ix_denuev_denunciante", columnList = "denunciante_id"),
         @Index(name = "ix_denuev_responsable", columnList = "responsable_id")
       })
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

    // N -> 1 Usuario (quien presenta la denuncia)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Usuario denunciante;

    // N -> 1 Usuario (quien atiende/gestiona la denuncia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;

    // 1 -> N Estados de la denuncia
    @OneToMany(mappedBy = "denuncia_evento",
               cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DenunciaEventoEstado> estados;
}
