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
public class DTOResultadoBusquedaEspacios{
    private Long id;
    private String nombre;
    private String tipo;
    private List<String> disciplinas;
}

