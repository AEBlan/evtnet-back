package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "solicitud_espacio_publico")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEspacioPublico extends Base {

    // -------- Atributos --------
    @Column(name = "nombre_espacio", nullable = false)
    private String nombreEspacio;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "latitud_ubicacion")
    private BigDecimal latitudUbicacion;

    @Column(name = "longitud_ubicacion")
    private BigDecimal longitudUbicacion;

    @Column(name = "direccion_ubicacion")
    private String direccionUbicacion;

    @Column(name = "fecha_hora_alta", nullable = false)
    private LocalDateTime fechaHoraAlta;

    @Column(name = "justificacion")
    private String justificacion;

    // -------- Relaciones --------

    // n..1: muchas solicitudes -> un usuario solicitante
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    // 1..n: una solicitud puede derivar en 0..n espacios creados a partir de ella
    @OneToMany(mappedBy = "solicitudEspacioPublico", fetch = FetchType.EAGER)
    private List<Espacio> espacios;

    // 1..n: una solicitud tiene 0..n estados SEP (historial)
    @OneToMany(mappedBy = "solicitudEspacioPublico", fetch = FetchType.EAGER)
    private List<SEPEstado> sepEstados;
}
