package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ParametroSistema;
import com.evtnet.evtnetback.dto.parametroSistema.DTOParametroSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParametroSistemaService extends BaseService <ParametroSistema, Long> {
    Page<DTOParametroSistema> obtenerListaParametroSistema(Pageable pageable) throws Exception;
    DTOParametroSistema obtenerParametroSistemaCompleto(Long id) throws Exception;
    void altaParametroSistema(DTOParametroSistema parametroSistema) throws Exception;
    void modificarParametroSistema(DTOParametroSistema parametroSistema) throws Exception;
    void bajaParametroSistema(Long id) throws Exception;
}
