package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOResenasEspacio {
    private Long id;
    private List<DTOResenaEspacio> resenas;
    private List<Puntuacion> puntuaciones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DTOResenaEspacio {
        private String titulo;
        private String comentario;
        private Integer puntuacion;
        private String username;
        private String usuario;
        private Long fecha;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Puntuacion{
        private Integer puntuacion;
        private long cantidad;
    }
}
