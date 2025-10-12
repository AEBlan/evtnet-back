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
public class DTOEspacioEditar {

    private Long id;
    private String nombre;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private List<DTOSubespacioEditar> subEspacios;
    private boolean esAdmin;
    private boolean esPropietario;
    private boolean esPublico;
    private DTOArchivo basesYCondiciones;
    private List<DTOArchivo> documentacion;
    private DTOEspacioEstado estado;
    private String username;
}

