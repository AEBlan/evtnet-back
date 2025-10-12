package com.evtnet.evtnetback.dto.supereventos;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOBusquedaEvento {
    private Long id;
    private String nombre;
    private Long fechaDesde;
    private Long fechaHasta;
    private String nombreEspacio;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DTODisciplinas { 
        private Long id; 
        private String nombre;
    }

    private List<DTODisciplinas> disciplinas;
}
