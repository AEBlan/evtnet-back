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
public class DTOResultadoBusquedaMisEspacios {
    private Long id;
    private String nombre;
    private String rol;
    private List<String> disciplinas;
}
