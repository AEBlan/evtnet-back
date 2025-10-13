package com.evtnet.evtnetback.dto.supereventos;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOSuperEventoEditar {
    private Long id;
    private String nombre;
    private String descripcion;

    private Boolean esOrganizador;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DTOEvento { 
        private Long id; 
        private String nombre;
        private Long fechaDesde;
        private Long fechaHasta;
        private String nombreEspacio;
        private Boolean crear;
        private Boolean eliminar;
        private Boolean cancelado;
    }

    private List<DTOEvento> eventos;
}
