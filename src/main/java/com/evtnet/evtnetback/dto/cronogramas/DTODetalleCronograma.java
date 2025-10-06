package com.evtnet.evtnetback.dto.cronogramas;

import java.util.List;

public record DTODetalleCronograma(
    String nombreEspacio,
    long fechaDesde,
    long fechaHasta,
    List<Horario> horarios
) {
    public record Horario(
        long id,
        int diaSemana,
        long horaDesde,
        long horaHasta,
        double precioOrganizacion
    ) {}
}
