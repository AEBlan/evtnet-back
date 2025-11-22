package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.IconoCaracteristica;
import com.evtnet.evtnetback.dto.espacios.DTOCaracteristicaSubEspacio;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IconoCaracteristicaService extends BaseService<IconoCaracteristica, Long> {
    //DTOIconoCaracteristica subirIcono(Long caracteristicaId, MultipartFile file);
    //void eliminarIcono(Long iconoId);
    Page<DTOIconoCaracteristica> obtenerListaIconoCaracteristica(int page, boolean vigentes, boolean dadasDeBaja) throws Exception;
    DTOIconoCaracteristica obtenerIconoCaracteristicaCompleto(Long id) throws Exception;
    void altaIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception;
    void modificarIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception;
    void bajaIconoCaracteristica(Long id) throws Exception;
    void restaurarIconoCaracteristica(Long id) throws Exception;

    IconoCaracteristica obtenerIconosEspacio(Long idEspacio)throws Exception;
    List<DTOCaracteristicaSubEspacio> obtenerCaracteristicasSubEspacio(Long idEspacio) throws Exception;
    List<DTOIconoCaracteristica> obtenerListaIcono() throws Exception;
}
