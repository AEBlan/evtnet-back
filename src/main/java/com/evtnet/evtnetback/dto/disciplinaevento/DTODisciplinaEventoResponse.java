package com.evtnet.evtnetback.dto.disciplinaevento;

import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODisciplinaEventoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private DTODisciplinaRef disciplina; // { id, nombre }
}

