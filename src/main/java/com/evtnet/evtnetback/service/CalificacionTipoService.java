package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.CalificacionTipo;
import java.util.List;

public interface CalificacionTipoService extends BaseService<CalificacionTipo, Long> {
    List<CalificacionTipo> findActivosOrdenados();
}
