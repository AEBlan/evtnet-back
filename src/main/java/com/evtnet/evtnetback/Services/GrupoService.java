package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Grupo;
import com.evtnet.evtnetback.dto.grupos.*;

import org.springframework.data.domain.Page;
import java.util.List;

import com.evtnet.evtnetback.Services.BaseService;

public interface GrupoService extends BaseService<Grupo, Long> {
    Page<DTOGrupoSimple> obtenerGrupos(String texto, int page);
    DTOAdminGrupo adminObtenerGrupo(Long id);
    List<DTOGrupoMisGrupos> obtenerMisGrupos();
    DTOGrupo obtenerGrupo(Long id) throws Exception;
    void salir(Long id) throws Exception;

    List<DTOBusquedaUsuario> buscarUsuariosParaAgregar(Long idGrupo, String texto) throws Exception;
    DTORespuestaCrearGrupo crearGrupo(DTOCrearGrupo dto) throws Exception;

    List<DTOTipoUsuarioGrupo> obtenerTiposUsuarioGrupo();

    DTOModificarGrupo obtenerDatosModificarGrupo(Long idGrupo) throws Exception;

    void modificarGrupo(DTOModificarGrupo dto) throws Exception;


}
