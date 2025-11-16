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
    private String pageSelector;
    private String selector;
    private List<DTOEventoMascota> eventos;
    private Integer longitud;
    private Long fechaAlta;
    private Long fechaBaja;
    private List<DTOInstanciaMascotaSecuencia> secuencia;
}