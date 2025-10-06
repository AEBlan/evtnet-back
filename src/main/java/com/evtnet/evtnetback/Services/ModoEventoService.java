package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModoEventoService extends BaseService <ModoEvento, Long> {
    List<DTOModoEvento> buscarPorNombre(String text) throws Exception;
    Page<DTOModoEvento> obtenerListaModosEvento(Pageable pageable) throws Exception;
    DTOModoEvento obtenerModoEventoCompleto(Long id) throws Exception;
    void altaModoEvento(DTOModoEvento modoEvento) throws Exception;
    void modificarModoEvento(DTOModoEvento modoEvento) throws Exception;
    void bajaModoEvento(Long id) throws Exception;
}
