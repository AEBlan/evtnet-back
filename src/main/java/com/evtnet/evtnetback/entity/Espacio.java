package com.evtnet.evtnetback.entity;

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

    private LocalDateTime fechaHoraAlta;
    private LocalDateTime fechaHoraBaja;

    @Column(name = "direccion_ubicacion")
    private String direccionUbicacion;

    @Column(name = "latitud_ubicacion")
    private BigDecimal latitudUbicacion;

    @Column(name = "longitud_ubicacion")
    private BigDecimal longitudUbicacion;

    @Column(name = "bases_y_condiciones")
    private String basesYCondiciones;

    @Column(name = "requiere_aprobar_eventos")
    private Boolean requiereAprobarEventos;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdministradorEspacio> administradoresEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ImagenEspacio> imagenesEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ResenaEspacio> resenasEspacio;

    @ManyToOne
    @JoinColumn(name = "tipo_espacio_id")
    private TipoEspacio tipoEspacio;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.EAGER)
    private List<SolicitudEspacioPublico> solicitudesEspacioPublico;

    @OneToOne(mappedBy = "espacio")
    private Chat chat;

    @OneToMany(mappedBy = "espacio")
    private List<EspacioEstado> espacioEstado;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentacionEspacio> documentacionEspacios;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEspacio> subEspacios;


}
