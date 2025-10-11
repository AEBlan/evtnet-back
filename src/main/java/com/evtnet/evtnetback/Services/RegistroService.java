package com.evtnet.evtnetback.Services;

import java.util.List;

import com.evtnet.evtnetback.Entities.Registro;
import com.evtnet.evtnetback.dto.registros.DTOFiltrosRegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistroMeta;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;

public interface RegistroService extends BaseService <Registro, Long> {
    List<DTORegistroMeta> obtenerRegistros() throws Exception;
    DTORegistroMeta obtenerRegistroFormateado(String nombre) throws Exception;

    List<DTORegistro> buscar(String registro, DTOFiltrosRegistro filtros) throws Exception;

    List<String> obtenerEntidades(String registro) throws Exception;
    List<String> obtenerAcciones(String registro) throws Exception;

    List<DTOBusquedaUsuario> buscarUsuarios(String texto) throws Exception;
}
