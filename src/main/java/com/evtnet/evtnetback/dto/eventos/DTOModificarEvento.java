package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.evtnet.evtnetback.json.IsoOrEpochLocalDateTimeDeserializer;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOModificarEvento {
    private long id;
    private String nombre;
    private String descripcion;

    private List<ItemIdNombre> disciplinas;

    private BigDecimal precioInscripcion;

    private int cantidadMaximaParticipantes;
    private int cantidadMaximaInvitados;

    private Boolean crearSuperevento;
    private Superevento superevento;

    private List<RangoReintegro> rangosReintegro;

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
