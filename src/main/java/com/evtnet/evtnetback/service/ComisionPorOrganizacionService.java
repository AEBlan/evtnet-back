package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComisionPorOrganizacion;
import com.evtnet.evtnetback.dto.comisionPorOrganizacion.DTOComisionPorOrganizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComisionPorOrganizacionService extends BaseService<ComisionPorOrganizacion, Long> {
    Page<DTOComisionPorOrganizacion> obtenerListaComisionPorOrganizacion(Pageable pageable) throws Exception;
    DTOComisionPorOrganizacion obtenerComisionPorOrganizacionCompleto(Long id) throws Exception;
    void altaComisionPorOrganizacion(DTOComisionPorOrganizacion comisionPorOrganizacion) throws Exception;
    void modificarComisionPorOrganizacion(DTOComisionPorOrganizacion comisionPorOrganizacion) throws Exception;
    void bajaComisionPorOrganizacion(Long id) throws Exception;
}
