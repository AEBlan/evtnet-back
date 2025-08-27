package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import java.util.List;

public interface ModoEventoService extends BaseService <ModoEvento, Long> {
    List<DTOModoEvento> buscarPorNombre(String text) throws Exception;
}
