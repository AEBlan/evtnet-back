package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "disciplina_espacio",
       indexes = {
         @Index(name = "ix_de_disciplina", columnList = "disciplina_id"),
         @Index(name = "ix_de_espacio", columnList = "espacio_id")
       })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaEspacio extends Base {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;
}
