package com.evtnet.evtnetback.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "disciplina_evento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaEvento extends Base {

    // 🔁 N DisciplinaEvento -> 1 Evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    @JsonBackReference("evento-disciplinasEvento")
    private Evento evento;

    // 🔁 N DisciplinaEvento -> 1 Disciplina
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

}
