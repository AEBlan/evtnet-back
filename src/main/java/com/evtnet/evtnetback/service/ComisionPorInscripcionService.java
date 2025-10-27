package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComisionPorInscripcion;
import com.evtnet.evtnetback.dto.comisionPorInscripcion.DTOComisionPorInscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComisionPorInscripcionService extends BaseService<ComisionPorInscripcion, Long> {
    Page<DTOComisionPorInscripcion> obtenerListaComisionPorInscripcion(Pageable pageable) throws Exception;
    DTOComisionPorInscripcion obtenerComisionPorInscripcionCompleto(Long id) throws Exception;
    void altaComisionPorInscripcion(DTOComisionPorInscripcion comisionPorInscripcion) throws Exception;
    void modificarComisionPorInscripcion(DTOComisionPorInscripcion comisionPorInscripcion) throws Exception;
    void bajaComisionPorInscripcion(Long id) throws Exception;
}
