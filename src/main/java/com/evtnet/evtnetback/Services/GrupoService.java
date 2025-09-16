package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Grupo;
import com.evtnet.evtnetback.dto.grupos.DTOGrupoSimple;
import org.springframework.data.domain.Page;

import com.evtnet.evtnetback.Services.BaseService;

public interface GrupoService extends BaseService<Grupo, Long> {
    Page<DTOGrupoSimple> obtenerGrupos(String texto, int page);

}
