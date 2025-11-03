package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;

import java.util.List;
import org.springframework.data.domain.Page;


public interface EventoService extends BaseService<Evento, Long> {  
    List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) throws Exception;
    List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro) throws Exception;
    DTOEventoDetalle obtenerEventoDetalle(long idEvento) throws Exception;
    DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacio) throws Exception;
    DTOPreferenciaPago pagarCreacionEvento(DTOEventoCreate req) throws Exception;
    long crearEvento(DTOEventoCreate req) throws Exception;
    int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis);

    // 👇 NUEVOS (para que @Override sea válido)
    DTOEventoParaInscripcion obtenerEventoParaInscripcion(long id) throws Exception;
    DTOVerificacionPrePago verificarDatosPrePago(DTOInscripcion dto) throws Exception;
    void inscribirse(DTOInscripcion dto) throws Exception;
    void desinscribirse(long idEvento) throws Exception;
    Number obtenerMontoDevolucionCancelacion(long idEvento, String username) throws Exception;
    DTODatosModificarEvento obtenerDatosModificacionEvento(long id) throws Exception;
    void modificarEvento(DTOModificarEvento dto) throws Exception;

    DTOInscripcionesEvento obtenerInscripciones(long idEvento, String texto) throws Exception;
    void cancelarInscripcion(long idInscripcion) throws Exception;
    DTODatosParaInscripcion obtenerDatosParaInscripcion(long idEvento, String username) throws Exception;
    List<DTOBusquedaUsuario> buscarUsuariosNoInscriptos(Long idEvento, String texto) throws Exception;
    void inscribirUsuario(DTOInscripcion dto) throws Exception;
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
    void aprobarRechazarEvento(Long idEvento, String estado);
    void cancelarEvento(Long idEvento);
}
