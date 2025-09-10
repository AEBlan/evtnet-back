// package com.evtnet.evtnetback.Services;
//
// import com.evtnet.evtnetback.Entities.Evento;
// import com.evtnet.evtnetback.dto.eventos.*;
// import java.util.List;
//
// public interface EventoService extends BaseService<Evento, Long> {
//     List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro);
//     List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro);
//     DTOEvento obtenerEventoDetalle(long idEvento);
//     DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacioOrNull);
//     long crearEvento(DTOEventoCreate req);
//     int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis);
// }
