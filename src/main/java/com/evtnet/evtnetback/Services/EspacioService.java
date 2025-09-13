package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;

// imports para #US_ESP_1
import com.evtnet.evtnetback.dto.espacios.DTOCrearEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioDetalle;
import com.evtnet.evtnetback.dto.comunes.IdResponse;


public interface EspacioService extends BaseService<Espacio, Long> {
    // #US_ESP_1: crear espacio privado (propietario = usuarioActualId)
    IdResponse crearEspacioPrivado(DTOCrearEspacio dto, Long usuarioActualId);

    // detalle para redirigir tras crear (#US_ESP_2)
    DTOEspacioDetalle detalle(Long espacioId);
}
