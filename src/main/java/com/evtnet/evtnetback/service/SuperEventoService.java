package com.evtnet.evtnetback.service;

import java.util.List;

import com.evtnet.evtnetback.entity.SuperEvento;
import com.evtnet.evtnetback.dto.supereventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;

public interface SuperEventoService extends BaseService <SuperEvento, Long>  {
    public List<DTOBusquedaAdministrados> buscarAdministrados(String text) throws Exception;

    public List<DTOResultadoBusquedaMisSuperEventos> buscarMisSuperEventos(DTOBusquedaMisSuperEventos data) throws Exception;

    public DTOSuperEvento obtenerSuperEvento(Long id) throws Exception;

    public long crearSuperEvento(DTOCrearSuperEvento data) throws Exception;

    public DTOSuperEventoEditar obtenerSuperEventoEditar(Long id) throws Exception;

    public void editarSuperEvento(DTOSuperEventoEditar data) throws Exception;

    public void dejarDeAdministrar(Long supereventoId) throws Exception;

    public void baja(Long supereventoId) throws Exception;

    public List<DTOBusquedaEvento> buscarEventosNoVinculados(Long idSuperevento, String texto) throws Exception;

    public DTOAdministradoresSuperevento obtenerAdministradores(Long idEvento) throws Exception;

    public void agregarAdministrador(Long idEvento, String username) throws Exception;

    public void quitarAdministrador(Long idEvento, String username) throws Exception;

    public void entregarOrganizador(Long idEvento, String username) throws Exception;

    public List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEvento, String texto) throws Exception;

    
}
