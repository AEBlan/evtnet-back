package com.evtnet.evtnetback.dto.solicitudesEspacio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOResultadoBusquedaSEP {
    private Long idSEP;
    private String nombreEspacio;
    private String estado;
    private Long fechaIngreso;
    private Long fechaUltimoCambioEstado;
    private Long idEspacio;
}
