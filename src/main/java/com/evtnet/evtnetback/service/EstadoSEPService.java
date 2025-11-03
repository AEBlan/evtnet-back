package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.EstadoSEP;
import com.evtnet.evtnetback.dto.estadoSEP.DTOEstadoSEP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EstadoSEPService extends BaseService<EstadoSEP, Long> {
    Page<DTOEstadoSEP> obtenerListaEstadoSEP(Pageable pageable) throws Exception;
    DTOEstadoSEP obtenerEstadoSEPCompleto(Long id) throws Exception;
    void altaEstadoSEP(DTOEstadoSEP estadoSEP) throws Exception;
    void modificarEstadoSEP(DTOEstadoSEP estadoSEP) throws Exception;
    void bajaEstadoSEP(Long id) throws Exception;
    List<DTOEstadoSEP> obtenerEstadosSEP() throws Exception;
}
