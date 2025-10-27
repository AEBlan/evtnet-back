package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.EstadoDenunciaEvento;
import com.evtnet.evtnetback.dto.estadoDenunciaEvento.DTOEstadoDenunciaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstadoDenunciaEventoService extends BaseService<EstadoDenunciaEvento, Long> {
    Page<DTOEstadoDenunciaEvento> obtenerListaEstadoDenunciaEvento(Pageable pageable) throws Exception;
    DTOEstadoDenunciaEvento obtenerEstadoDenunciaEventoCompleto(Long id) throws Exception;
    void altaEstadoDenunciaEvento(DTOEstadoDenunciaEvento estadoDenunciaEvento) throws Exception;
    void modificarEstadoDenunciaEvento(DTOEstadoDenunciaEvento estadoDenunciaEvento) throws Exception;
    void bajaEstadoDenunciaEvento(Long id) throws Exception;
    
}
