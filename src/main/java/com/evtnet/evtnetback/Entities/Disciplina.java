package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "disciplina",
       uniqueConstraints = @UniqueConstraint(name = "uk_disciplina_nombre", columnNames = "nombre"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disciplina extends Base {

    @Column(name = "nombre", nullable = false) private String nombre;
    @Column(name = "descripcion")              private String descripcion;
    @Column(name = "fecha_hora_alta")          private LocalDateTime fechaHoraAlta;
    @Column(name = "fecha_hora_baja")          private LocalDateTime fechaHoraBaja;

    // 1 → N con DisciplinaEspacio
    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisciplinaEspacio> disciplinasEspacio;

    // 1 → N con DisciplinaEvento
    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY)
    private List<DisciplinaEvento> disciplinasEvento;
}
