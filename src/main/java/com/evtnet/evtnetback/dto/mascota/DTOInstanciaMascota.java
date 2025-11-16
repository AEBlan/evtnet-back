package com.evtnet.evtnetback.dto.mascota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOInstanciaMascota {
    private Long id;
    private String nombre;
    private String descripcion;
    private String pageRegex;
    private String selector;
    private String events;
    private Integer longitud;
    private Long fechaAlta;
    private Long fechaBaja;
    private List<DTOInstanciaMascotaSecuencia> secuencia;
}