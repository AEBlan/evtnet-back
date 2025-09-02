package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estado_denuncia_evento",
       uniqueConstraints = @UniqueConstraint(name = "uk_estado_denuncia_nombre", columnNames = "nombre"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoDenunciaEvento extends Base {

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    // 1 -> N DenunciaEventoEstado (inverso)
    @OneToMany(mappedBy = "estadoDenunciaEvento")
    private List<DenunciaEventoEstado> denunciasEventoEstado;
}
