package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.CalificacionTipo;
import java.util.List;

public interface CalificacionTipoService extends BaseService<CalificacionTipo, Long> {
    List<CalificacionTipo> findActivosOrdenados();
}
