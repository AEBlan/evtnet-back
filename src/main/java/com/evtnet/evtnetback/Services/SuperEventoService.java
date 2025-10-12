package com.evtnet.evtnetback.Services;

import java.util.List;

import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.dto.supereventos.*;

public interface SuperEventoService extends BaseService <SuperEvento, Long>  {
    public List<DTOBusquedaAdministrados> buscarAdministrados(String text) throws Exception;

    public List<DTOResultadoBusquedaMisSuperEventos> buscarMisSuperEventos(DTOBusquedaMisSuperEventos data) throws Exception;

    public DTOSuperEvento obtenerSuperEvento(Long id) throws Exception;

    public long crearSuperEvento(DTOCrearSuperEvento data) throws Exception;
}
