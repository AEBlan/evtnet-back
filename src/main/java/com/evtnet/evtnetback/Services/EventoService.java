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
    long crearEvento(DTOEventoCreate req);
    int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis);

    // 👇 NUEVOS (para que @Override sea válido)
    DTOEventoParaInscripcion obtenerEventoParaInscripcion(long id);
    boolean verificarDatosPrePago(DTOInscripcion dto);
    void inscribirse(DTOInscripcion dto);
    void desinscribirse(long idEvento);
    Number obtenerMontoDevolucionCancelacion(long idEvento, String username);
    DTOModificarEvento obtenerDatosModificacionEvento(long id);
    void modificarEvento(DTOModificarEvento dto);
    List<DTOBusquedaUsuario> buscarUsuariosNoInscriptos(Long idEvento, String texto);


    DTOInscripcionesEvento obtenerInscripciones(long idEvento, String texto);
    void cancelarInscripcion(long idInscripcion);
    DTODatosParaInscripcion obtenerDatosParaInscripcion(long idEvento, String username);
    void inscribirUsuario(DTOInscripcion dto);


    DTOAdministradores obtenerAdministradores(long idEvento, String currentUser);
    void agregarAdministrador(long idEvento, String username);
    void quitarAdministrador(long idEvento, String username);
    void entregarOrganizador(long idEvento, String nuevoOrganizador);

    // DENUNCIAS
    void denunciarEvento(DTODenunciaEvento dto, String username);
    Page<DTODenunciaEventoSimple> buscarDenuncias(DTOBusquedaDenunciasEventos filtro, int page);
    DTODenunciaEventoCompleta obtenerDenunciaCompleta(long idDenuncia);
    DTODatosParaCambioEstadoDenuncia obtenerDatosParaCambioEstado(long idDenuncia);
    void cambiarEstadoDenuncia(DTOCambioEstadoDenuncia dto, String username);
    DTODatosParaDenunciarEvento obtenerDatosParaDenunciar(long idEvento, String username);


}
