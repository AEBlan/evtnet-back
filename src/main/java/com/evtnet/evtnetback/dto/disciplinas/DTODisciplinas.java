package com.evtnet.evtnetback.dto.disciplinas;

import com.google.api.client.util.DateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODisciplinas {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long fechaAlta;
    private Long fechaBaja;
}
