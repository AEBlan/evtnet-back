package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.TipoCalificacion;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacion;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacionSelect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TipoCalificacionService extends BaseService <TipoCalificacion, Long> {
    Page<DTOTipoCalificacion> obtenerListaTipoCalificacion(Pageable pageable) throws Exception;
    List<DTOTipoCalificacionSelect> obtenerTiposCalificacionSelect()throws Exception;
    DTOTipoCalificacion obtenerTipoCalificacionCompleto(Long id) throws Exception;
    void altaTipoCalificacion(DTOTipoCalificacion tipoCalificacion) throws Exception;
    void modificarTipoCalificacion(DTOTipoCalificacion tipoCalificacion) throws Exception;
    void bajaTipoCalificacion(Long id) throws Exception;
}
