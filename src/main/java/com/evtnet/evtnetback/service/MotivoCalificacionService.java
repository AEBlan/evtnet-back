package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.MotivoCalificacion;
import com.evtnet.evtnetback.dto.motivoCalificacion.DTOMotivoCalificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MotivoCalificacionService extends BaseService <MotivoCalificacion, Long> {
    Page<DTOMotivoCalificacion> obtenerListaMotivoCalificacion(Pageable pageable) throws Exception;
    DTOMotivoCalificacion obtenerMotivoCalificacionCompleto(Long id) throws Exception;
    void altaMotivoCalificacion(DTOMotivoCalificacion motivoCalificacion) throws Exception;
    void modificarMotivoCalificacion(DTOMotivoCalificacion motivoCalificacion) throws Exception;
    void bajaMotivoCalificacion(Long id) throws Exception;
}
