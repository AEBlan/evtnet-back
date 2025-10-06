package com.evtnet.evtnetback.dto.cronogramas;

import java.util.List;

public record DTOCronogramasEspacio(
    String nombre,
    List<Cronograma> cronogramas
) {
    public record Cronograma(
        long id,
        long fechaDesde,
        long fechaHasta,
        int diasHaciaAdelante
    ) {}
}
