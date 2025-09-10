package com.evtnet.evtnetback.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Data
@ToString(exclude = {"evento", "disciplina"})
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "DisciplinaEvento")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisciplinaEvento extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    // 🔁 N DisciplinaEvento -> 1 Evento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    @JsonBackReference("evento-disciplinasEvento")
    private Evento evento;

    // 🔁 N DisciplinaEvento -> 1 Disciplina
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disciplina_id", nullable = false)
    private Disciplina disciplina;

    // 👉 Si más adelante agregás atributos propios de la relación, van aquí
    // private String nivel;
    // private String genero;
}
