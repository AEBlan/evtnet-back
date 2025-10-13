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
public class DTOEspacio {
    private String nombre;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private String tipoEspacio;
    private List<DTOSubespacioDetalle> subEspacios;
    private int cantidadImagenes;
    private boolean esAdmin;
    private Long idChat;
    private DTOEspacioEstado estado;
}
