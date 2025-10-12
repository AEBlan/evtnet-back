package com.evtnet.evtnetback.dto.espacios;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearEspacio {
    private String nombre;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private boolean requiereAprobarEventos;
    private List<DTOSubespacio> subEspacios;
    private boolean esPublico;
    private Long sepId;
    private String username;
}
