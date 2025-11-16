package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.DTOInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOAltaInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOModificarInstanciaMascota;
import com.evtnet.evtnetback.entity.InstanciaMascota;
import org.springframework.data.domain.Page;

public interface InstanciaMascotaService extends BaseService<InstanciaMascota, Long> {
    Page<DTOInstanciaMascota> obtenerListaInstanciaMascota(int page, String texto) throws Exception;
    DTOInstanciaMascota obtenerInstanciaMascotaCompleta(Long id) throws Exception;
    void altaInstanciaMascota(DTOAltaInstanciaMascota instanciaMascota) throws Exception;
    void modificarInstanciaMascota(DTOModificarInstanciaMascota instanciaMascota) throws Exception;
    void bajaInstanciaMascota(Long id) throws Exception;
}