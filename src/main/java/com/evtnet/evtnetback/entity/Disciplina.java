package com.evtnet.evtnetback.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "disciplina")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Disciplina extends Base {

    @Column(name = "nombre", nullable = false) 
    private String nombre;
    @Column(name = "descripcion")              
    private String descripcion;
    
    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    // 1 → N con DisciplinaEvento
    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY)
    private List<DisciplinaEvento> disciplinasEvento;

    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY)
    private List<DisciplinaSubEspacio> disciplinasSubespacio;
}
