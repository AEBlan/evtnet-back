package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOModificarEvento {
    private Long id;
    private String nombre;
    private String descripcion;

    private Long idEspacio;                
    private String nombreEspacio;          
    private boolean usarCronograma;

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

    private Long horarioId;                
    private BigDecimal precioOrganizacion; 

    private String direccion;
    private Ubicacion ubicacion;

    private List<ItemIdNombre> disciplinas;
    private List<ItemIdNombre> modos;

    private List<TipoInscripcion> tiposInscripcion;

    private BigDecimal precioInscripcion;
    private BigDecimal comisionInscripcion;

    private Integer cantidadMaximaParticipantes;
    private Integer cantidadMaximaInvitados;

    private Integer cantidadParticipantesActual;
    private Integer cantidadMaximaInvitadosPorInvitacionEfectiva;

    private Boolean crearSuperevento;
    private Superevento superevento; 

    private List<RangoReintegro> rangosReintegro;

    private Boolean espacioPublico;
    private Boolean administradorEspacio;

    private Boolean administradorEvento;
    private Boolean organizadorEvento;

    private Integer diasHaciaAdelante;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Ubicacion { private Double latitud; private Double longitud; }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemIdNombre { private Long id; private String nombre; }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TipoInscripcion { private Long id; private String nombre; private boolean seleccionado; }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Superevento { private Long id; private String nombre; private String descripcion; }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RangoReintegro { private int dias; private int horas; private int minutos; private int porcentaje; }
}
