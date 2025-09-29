package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOModificarEvento {
    private long id;                     // ⚡ nunca null
    private String nombre;               // ⚡ vacío si no hay
    private String descripcion;          // ⚡ vacío si no hay

    private long idEspacio;              // ⚡ 0 si no hay
    private String nombreEspacio;        // ⚡ vacío si no hay
    private boolean usarCronograma;      // ⚡ siempre boolean

    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

    private Long horarioId;              // ⚡ 0 si no hay
    private BigDecimal precioOrganizacion; 

    private String direccion;            // ⚡ vacío si no hay
    private Ubicacion ubicacion;

    private List<ItemIdNombre> disciplinas;   // ⚡ lista vacía si no hay
    private List<ItemIdNombre> modos;         // ⚡ lista vacía
    private List<TipoInscripcion> tiposInscripcion; // ⚡ lista vacía

    private BigDecimal precioInscripcion;
    private BigDecimal comisionInscripcion;

    private int cantidadMaximaParticipantes; // ⚡ 0 si no hay
    private int cantidadMaximaInvitados;     // ⚡ 0 si no hay

    private int cantidadParticipantesActual; // ⚡ 0 si no hay
    private int cantidadMaximaInvitadosPorInvitacionEfectiva; // ⚡ 0 si no hay

    private boolean crearSuperevento;        // ⚡ siempre boolean
    private Superevento superevento;         // ⚡ objeto vacío, nunca null

    private List<RangoReintegro> rangosReintegro; // ⚡ lista vacía

    private Boolean espacioPublico;          // ⚡ siempre boolean
    private boolean administradorEspacio;    // ⚡ siempre boolean

    private boolean administradorEvento;     // ⚡ siempre boolean
    private boolean organizadorEvento;       // ⚡ siempre boolean

    private int diasHaciaAdelante;           // ⚡ default 0 si no hay

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Ubicacion { 
        private double latitud;   // ⚡ 0.0 si no hay
        private double longitud;  // ⚡ 0.0 si no hay
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemIdNombre { 
        private long id;          // ⚡ 0 si no hay
        private String nombre;    // ⚡ vacío si no hay
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TipoInscripcion { 
        private long id;          // ⚡ 0 si no hay
        private String nombre;    // ⚡ vacío si no hay
        private boolean seleccionado; 
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Superevento { 
        private long id;          // ⚡ 0 si no hay
        private String nombre;    // ⚡ vacío si no hay
        private String descripcion; // ⚡ vacío si no hay
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RangoReintegro { 
        private int dias; 
        private int horas; 
        private int minutos; 
        private int porcentaje; 
    }
}
