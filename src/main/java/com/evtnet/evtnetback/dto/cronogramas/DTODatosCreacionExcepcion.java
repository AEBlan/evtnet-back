// cronogramas/DTODatosCreacionExcepcion.java
package com.evtnet.evtnetback.dto.cronogramas;

import java.util.List;

public record DTODatosCreacionExcepcion(
    String nombreEspacio,
    long fechaDesde,
    long fechaHasta,
    List<TipoExcepcion> tiposExcepcion
) {
    public record TipoExcepcion(long id, String nombre) {}
}

