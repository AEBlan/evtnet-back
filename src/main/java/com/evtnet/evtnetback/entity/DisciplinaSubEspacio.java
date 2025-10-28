package com.evtnet.evtnetback.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "disciplina_subespacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaSubEspacio extends Base {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subespacio_id", nullable = false)
    private SubEspacio subEspacio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;
}