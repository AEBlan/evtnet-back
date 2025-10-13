package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import com.evtnet.evtnetback.dto.espacios.DTOCaracteristicaSubEspacio;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;           // <-- import Multipart

import java.util.List;

public interface IconoCaracteristicaService extends BaseService<IconoCaracteristica, Long> {
    //DTOIconoCaracteristica subirIcono(Long caracteristicaId, MultipartFile file);
    //void eliminarIcono(Long iconoId);
    Page<DTOIconoCaracteristica> obtenerListaIconoCaracteristica(Pageable pageable) throws Exception;
    DTOIconoCaracteristica obtenerIconoCaracteristicaCompleto(Long id) throws Exception;
    void altaIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception;
    void modificarIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception;
    void bajaIconoCaracteristica(Long id) throws Exception;

    IconoCaracteristica obtenerIconosEspacio(Long idEspacio)throws Exception;
    List<DTOCaracteristicaSubEspacio> obtenerCaracteristicasSubEspacio(Long idEspacio) throws Exception;
    List<DTOIconoCaracteristica> obtenerListaIcono() throws Exception;
}
