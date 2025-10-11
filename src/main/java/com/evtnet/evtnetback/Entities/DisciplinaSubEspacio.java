package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import com.evtnet.evtnetback.Entities.*;
import java.time.LocalDateTime;
import java.util.List;

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