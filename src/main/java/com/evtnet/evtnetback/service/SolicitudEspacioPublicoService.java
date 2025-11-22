package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.solicitudesEspacio.*;
import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SolicitudEspacioPublicoService extends BaseService <SolicitudEspacioPublico, Long> {
    void crearSolicitudEspacioPublico(DTOCrearSolicitudEspacio dtoSolicitud, String username)throws Exception;
    Page<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPublicos(DTOBusquedaSEP dtoBusquedaSEP, int page)throws Exception;
    DTOSolicitudCompleta obtenerDetalleSolcitudEP(Long idSEP)throws Exception;
    void cambiarEstadoSEP(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception;
    List<DTOEspacioPublico> obtenerEspacioParaSolicitud()throws Exception;
    void vincularEspacioASolicitud(Long idSEP, Long idEspacio)throws Exception;
    Page<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPrivados(DTOBusquedaSEP dtoBusquedaSEP, int page)throws Exception;
    DTOEspacioPrivadoCompleto obtenerDetalleSolcitudEPrivado(Long idSEP)throws Exception;
    void cambiarEstadoSEPrivado(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception;
    byte[] generarDocumentacionZip(Long idEspacio) throws Exception;
}
