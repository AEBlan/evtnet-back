package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ParametroSistema;
import com.evtnet.evtnetback.dto.parametroSistema.DTOParametroSistema;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParametroSistemaService extends BaseService <ParametroSistema, Long> {
    Page<DTOParametroSistema> obtenerListaParametroSistema(Pageable pageable) throws Exception;
    DTOParametroSistema obtenerParametroSistemaCompleto(Long id) throws Exception;
    void altaParametroSistema(DTOParametroSistema parametroSistema) throws Exception;
    void modificarParametroSistema(DTOParametroSistema parametroSistema) throws Exception;
    void bajaParametroSistema(Long id) throws Exception;
    BigDecimal getDecimal(String key, BigDecimal def);
    Integer getInt(String key, Integer def);

}
