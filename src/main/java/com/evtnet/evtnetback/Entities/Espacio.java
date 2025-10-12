package com.evtnet.evtnetback.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.evtnet.evtnetback.Entities.*;

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

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdministradorEspacio> administradoresEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ImagenEspacio> imagenesEspacio;

    @OneToMany(mappedBy = "espacio")
    private List<ResenaEspacio> resenasEspacio;

    @ManyToOne
    @JoinColumn(name = "tipo_espacio_id")
    private TipoEspacio tipoEspacio;

    @ManyToOne
    @JoinColumn(name = "solicitud_espacio_publico_id")
    private SolicitudEspacioPublico solicitudEspacioPublico;

    @OneToOne(mappedBy = "espacio")
    private Chat chat;

    @OneToMany(mappedBy = "espacio")
    private List<EspacioEstado> espacioEstado;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentacionEspacio> documentacionEspacios;

    @OneToMany(mappedBy = "espacio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubEspacio> subEspacios;


}
