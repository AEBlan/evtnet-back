package com.evtnet.evtnetback.dto.disciplinas;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODisciplinaRef {
    private Long id;       // id de Disciplina existente
    private String nombre; // opcional en responses
}

