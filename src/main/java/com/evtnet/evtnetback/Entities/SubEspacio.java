package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.evtnet.evtnetback.Entities.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subespacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubEspacio extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "capacidad_maxima")
    private int capacidadmaxima;

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @OneToMany(mappedBy = "subEspacio", fetch = FetchType.LAZY)
    private List<DisciplinaSubEspacio> disciplinasSubespacio;

    @OneToMany(mappedBy = "subEspacio", fetch = FetchType.LAZY)
    private List<Caracteristica> caracteristicas;

    @OneToMany(mappedBy = "subEspacio", fetch = FetchType.LAZY)
    private List<ConfiguracionHorarioEspacio> configuracionesHorarioEspacio;

    @OneToMany(mappedBy = "subEspacio", fetch = FetchType.LAZY)
    private List<Evento> eventos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encargado_subespacio_id")
    private EncargadoSubEspacio encargadoSubEspacio;

//    @OneToMany(mappedBy = "subEspacio")
//    private List<EncargadoSubEspacio> encargadosSubEspacio;
}
