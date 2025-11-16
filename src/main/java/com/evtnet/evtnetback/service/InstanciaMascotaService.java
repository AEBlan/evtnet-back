package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.*;
import com.evtnet.evtnetback.entity.InstanciaMascota;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InstanciaMascotaService extends BaseService<InstanciaMascota, Long> {
    Page<DTOInstanciaMascota> obtenerListaInstanciaMascota(int page, String texto) throws Exception;
    DTOInstanciaMascota obtenerInstanciaMascotaCompleta(Long id) throws Exception;
    void altaInstanciaMascota(DTOAltaInstanciaMascota instanciaMascota) throws Exception;
    void modificarInstanciaMascota(DTOModificarInstanciaMascota instanciaMascota) throws Exception;
    void bajaInstanciaMascota(Long id) throws Exception;
    List<DTOEventoMascota> obtenerEventosMascota() throws Exception;
    List<DTOInstanciaMascotaPagina> obtenerInstanciasParaPagina(String url) throws Exception;
    void registrarVisualizacion(Long idInstancia);

}