package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


public interface EventoService extends BaseService<Evento, Long> { /* 
    List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro);
    List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro, String username);
    DTOEvento obtenerEventoDetalle(long idEvento);
    DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacioOrNull);
    long crearEvento(DTOEventoCreate req);
    int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis);

    // 👇 NUEVOS (para que @Override sea válido)
    DTOEventoParaInscripcion obtenerEventoParaInscripcion(long id);
    DTOVerificacionPrePago verificarDatosPrePago(DTOInscripcion dto) throws Exception;
    void inscribirse(DTOInscripcion dto) throws Exception;
    void desinscribirse(long idEvento) throws Exception;
    Number obtenerMontoDevolucionCancelacion(long idEvento, String username);
    DTOModificarEvento obtenerDatosModificacionEvento(long id);
    void modificarEvento(DTOModificarEvento dto);*/
}
