package com.evtnet.evtnetback.Services;

import java.util.List;

import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaAdministrados;

public interface SuperEventoService extends BaseService <SuperEvento, Long>  {
    public List<DTOBusquedaAdministrados> buscarAdministrados(String text) throws Exception;
}
