package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.solicitudesEspacio.*;
import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;

import java.util.List;

public interface SolicitudEspacioPublicoService extends BaseService <SolicitudEspacioPublico, Long> {
    void crearSolicitudEspacioPublico(DTOCrearSolicitudEspacio dtoSolicitud, String username)throws Exception;
    List<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPublicos(DTOBusquedaSEP dtoBusquedaSEP)throws Exception;
    DTOSolicitudCompleta obtenerDetalleSolcitudEP(Long idSEP)throws Exception;
    void cambiarEstadoSEP(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception;
    List<DTOEspacioPublico> obtenerEspacioParaSolicitud()throws Exception;
    void vincularEspacioASolicitud(Long idSEP, Long idEspacio)throws Exception;
    List<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPrivados(DTOBusquedaSEP dtoBusquedaSEP)throws Exception;
    DTOEspacioPrivadoCompleto obtenerDetalleSolcitudEPrivado(Long idSEP)throws Exception;
    void cambiarEstadoSEPrivado(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception;
    byte[] generarDocumentacionZip(Long idEspacio) throws Exception;
}
