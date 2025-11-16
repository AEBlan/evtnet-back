package com.evtnet.evtnetback.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "evento_mascota")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoMascota extends Base {
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "valor", nullable = false)
    private String valor;

    private LocalDateTime fechaHoraBaja;

    @ManyToMany(mappedBy = "eventos")
    private List<InstanciaMascota> instancias;
}
