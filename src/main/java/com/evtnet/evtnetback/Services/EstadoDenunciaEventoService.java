package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.EstadoDenunciaEvento;
import com.evtnet.evtnetback.dto.estadoDenunciaEvento.DTOEstadoDenunciaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EstadoDenunciaEventoService extends BaseService<EstadoDenunciaEvento, Long> {
    Page<DTOEstadoDenunciaEvento> obtenerListaEstadoDenunciaEvento(Pageable pageable) throws Exception;
    DTOEstadoDenunciaEvento obtenerEstadoDenunciaEventoCompleto(Long id) throws Exception;
    void altaEstadoDenunciaEvento(DTOEstadoDenunciaEvento EestadoDenunciaEvento) throws Exception;
    void modificarEstadoDenunciaEvento(DTOEstadoDenunciaEvento estadoDenunciaEvento) throws Exception;
    void bajaEstadoDenunciaEvento(Long id) throws Exception;
    
}
