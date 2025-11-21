package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Grupo;
import com.evtnet.evtnetback.dto.grupos.*;

import org.springframework.data.domain.Page;
import java.util.List;

public interface GrupoService extends BaseService<Grupo, Long> {
    Page<DTOGrupoSimple> obtenerGrupos(String texto, int page);
    DTOAdminGrupo adminObtenerGrupo(Long id);
    List<DTOGrupoMisGrupos> obtenerMisGrupos();
    DTOGrupo obtenerGrupo(Long id) throws Exception;
    void salir(Long id) throws Exception;

    List<DTOBusquedaUsuario> buscarUsuariosParaAgregar(Long idGrupo, String texto) throws Exception;
    DTORespuestaCrearGrupo crearGrupo(DTOCrearGrupo dto) throws Exception;

    void toggleInvitacion(Long idGrupo, Boolean aceptar) throws Exception;

    List<DTOTipoUsuarioGrupo> obtenerTiposUsuarioGrupo();

    DTOModificarGrupo obtenerDatosModificarGrupo(Long idGrupo) throws Exception;

    void modificarGrupo(DTOModificarGrupo dto) throws Exception;


}
