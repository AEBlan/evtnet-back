package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


public interface EventoService extends BaseService<Evento, Long> {  
    List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro);
    List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro, String username);
    DTOEvento obtenerEventoDetalle(long idEvento);
    DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacioOrNull);
    long crearEvento(DTOEventoCreate req) throws Exception;
    int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis);

    // 👇 NUEVOS (para que @Override sea válido)
    DTOEventoParaInscripcion obtenerEventoParaInscripcion(long id);
    DTOVerificacionPrePago verificarDatosPrePago(DTOInscripcion dto) throws Exception;
    void inscribirse(DTOInscripcion dto) throws Exception;
    void desinscribirse(long idEvento) throws Exception;
    Number obtenerMontoDevolucionCancelacion(long idEvento, String username);
    DTOModificarEvento obtenerDatosModificacionEvento(long id) throws Exception;
    void modificarEvento(DTOModificarEvento dto);

    DTOInscripcionesEvento obtenerInscripciones(long idEvento, String texto) throws Exception;
    void cancelarInscripcion(long idInscripcion);
    DTODatosParaInscripcion obtenerDatosParaInscripcion(long idEvento, String username) throws Exception;
    List<DTOBusquedaUsuario> buscarUsuariosNoInscriptos(Long idEvento, String texto);
    void inscribirUsuario(DTOInscripcion dto);
    DTOAdministradores obtenerAdministradores(long idEvento, String currentUser) throws Exception;
    List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEvento, String texto);
    void agregarAdministrador(long idEvento, String username);
    void quitarAdministrador(long idEvento, String username);
    void entregarOrganizador(long idEvento, String nuevoOrganizador) throws Exception;
    void denunciarEvento(DTODenunciaEvento dto, String username);
    Page<DTODenunciaEventoSimple> buscarDenuncias(DTOBusquedaDenunciasEventos filtro, int page) throws Exception;
    DTODenunciaEventoCompleta obtenerDenunciaCompleta(long idDenuncia) throws Exception;
    DTODatosParaCambioEstadoDenuncia obtenerDatosParaCambioEstado(long idDenuncia);
    void cambiarEstadoDenuncia(DTOCambioEstadoDenuncia dto, String username);
    DTODatosParaDenunciarEvento obtenerDatosParaDenunciar(long idEvento, String username);
}
