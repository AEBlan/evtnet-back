package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.MedioDePago;
import com.evtnet.evtnetback.dto.medioDePago.DTOMedioDePago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedioDePagoService extends BaseService <MedioDePago, Long> {
    Page<DTOMedioDePago> obtenerListaMedioDePago(Pageable pageable) throws Exception;
    DTOMedioDePago obtenerMedioDePagoCompleto(Long id) throws Exception;
    void altaMedioDePago(DTOMedioDePago medioDePago) throws Exception;
    void modificarMedioDePago(DTOMedioDePago medioDePago) throws Exception;
    void bajaMedioDePago(Long id) throws Exception;
}
