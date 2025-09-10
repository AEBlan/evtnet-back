// cronogramas/DTODatosCreacionHorario.java
package com.evtnet.evtnetback.dto.cronogramas;

public record DTODatosCreacionHorario(
    String nombreEspacio,
    long fechaDesde,
    long fechaHasta,
    double comision
) {}

