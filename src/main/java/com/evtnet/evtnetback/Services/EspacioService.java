package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;

// imports para #US_ESP_1
import com.evtnet.evtnetback.dto.espacios.*;
import com.evtnet.evtnetback.dto.eventos.DTOAdministradores;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface EspacioService extends BaseService<Espacio, Long> {
    Long crearEspacio(DTOCrearEspacio dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception;
    DTOEspacio obtenerEspacio(Long id, String username) throws Exception;
    DTOEspacioEditar obtenerEspacioEditar(Long id, String username) throws Exception;
    void editarEspacio(DTOEspacioEditar dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception;
    void dejarDeAdministrar(Long id, String username) throws Exception;
    String obtenerNombreEspacio(Long id)throws Exception;
    List<DTOTipoEspacio> obtenerTiposEspacio() throws Exception;
    List<DTOResultadoBusquedaEspacios> buscarEspacios(DTOBusquedaEspacios dtoEspacio) throws Exception;
    List<DTOResultadoBusquedaMisEspacios> buscarMisEspacios(DTOBusquedaMisEspacios dtoEspacio, String username) throws Exception;
    List<DTOResultadoBusquedaEventosPorEspacio> buscarEventosPorEspacio(DTOBusquedaEventosPorEspacio dtoBusquedaEventos)throws Exception;
    List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEspacio, String texto)throws Exception;
    DTOAdministradoresEspacio obtenerAdministradoresEspacio(Long idEspacio, String username)throws Exception;
    void eliminarAdministradorEspacio (Long idEspacio, String username)throws Exception;
    void agregarAdministradorEspacio (Long idEspacio, String username)throws Exception;
    void entregarPropietario (Long idEspacio, String username, String usernamePropietario)throws Exception;
    void agregarEncargadoSubespacio (Long idSubEspacio, String username)throws Exception;
    List<DTOEncargadoSubespacio>obtenerEncargadosSubespacios(Long idEspacio)throws Exception;
    //List<DTOEstadoEspacio>obtenerEstadosEspacio() throws Exception;
}
