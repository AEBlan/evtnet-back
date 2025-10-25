package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.json.IsoOrEpochLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTODatosModificarEvento {
    private String nombre;               // ⚡ vacío si no hay
    private String descripcion;          // ⚡ vacío si no hay

    private String nombreEspacio;
    private String nombreSubespacio;

    private Long fechaHoraDesde;

    private Long fechaHoraHasta;

    private BigDecimal adicionalPorInscripcion;

    private List<ItemIdNombre> disciplinas;

    private BigDecimal precioInscripcion;
    private BigDecimal comisionInscripcion;

    private int cantidadMaximaParticipantes;
    private int cantidadMaximaInvitados;

    private int cantidadParticipantesActual;
    private int cantidadMaximaInvitadosPorInvitacionEfectiva;

    private Superevento superevento;

    private List<RangoReintegro> rangosReintegro;

    private Boolean espacioPublico;
    private boolean administradorEspacio;

    private boolean administradorEvento;
    private boolean organizadorEvento;



    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ItemIdNombre { 
        private Long id;
        private String nombre;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Superevento { 
        private Long id;
        private String nombre;
        private String descripcion;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RangoReintegro { 
        private int dias; 
        private int horas; 
        private int minutos; 
        private int porcentaje; 
    }
}
