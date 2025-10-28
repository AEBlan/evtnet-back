package com.evtnet.evtnetback.entity;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denunciante_id", nullable = false)
    private Usuario denunciante;

    @OneToMany(mappedBy = "denunciaEvento")
    private List<DenunciaEventoEstado> estados;
}
