package com.evtnet.evtnetback.Entities;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "espacio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Espacio extends Base {

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_hora_alta")
    private LocalDateTime fechaHoraAlta;

    @Column(name = "fecha_hora_baja")
    private LocalDateTime fechaHoraBaja;

    @Column(name = "direccion_ubicacion")
    private String direccionUbicacion;

    @Column(name = "latitud_ubicacion")
    private BigDecimal latitudUbicacion;

    @Column(name = "longitud_ubicacion")
    private BigDecimal longitudUbicacion;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdministradorEspacio> administradoresEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ImagenEspacio> imagenesEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ConfiguracionHorarioEspacio> configuracionesHorario;

    @OneToMany(mappedBy = "espacio")
    private List<ResenaEspacio> resenasEspacio;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisciplinaEspacio> disciplinasEspacio;

    @ManyToOne
    @JoinColumn(name = "tipo_espacio_id")
    private TipoEspacio tipoEspacio;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Caracteristica> caracteristicas;

    @ManyToOne
    @JoinColumn(name = "solicitud_espacio_publico_id")
    private SolicitudEspacioPublico solicitudEspacioPublico;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chat> chats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario;


}
