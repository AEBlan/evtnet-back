package com.evtnet.evtnetback.dto.disciplinaevento;

import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODisciplinaEventoCreate {
    private String nombre;         // si tu front lo envía, si no podés omitirlo
    private String descripcion;    // idem
    private DTODisciplinaRef disciplina; // al menos disciplina.id
    // Ej: { "disciplina": { "id": 5 } }
}

