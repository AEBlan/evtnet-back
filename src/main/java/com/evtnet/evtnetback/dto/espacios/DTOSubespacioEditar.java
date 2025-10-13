package com.evtnet.evtnetback.dto.espacios;

import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOSubespacioEditar {
    private Long id;
    private String nombre;
    private String descripcion;
    private int capacidadMaxima;
    private List<DTODisciplinas> disciplinas;
}
