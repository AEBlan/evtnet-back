// cronogramas/DTOExcepcionesCronograma.java
package com.evtnet.evtnetback.dto.cronogramas;

import java.util.List;

public record DTOExcepcionesCronograma(
    String nombreEspacio,
    long fechaDesde,
    long fechaHasta,
    List<Excepcion> excepciones
) {
    public record Excepcion(
        long id,
        long fechaHoraDesde,
        long fechaHoraHasta,
        String tipo,
        boolean hayEventosProgramados
    ) {}
}
