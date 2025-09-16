package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Services.GrupoServiceImpl;
import com.evtnet.evtnetback.dto.grupos.DTOGrupoSimple;
import com.evtnet.evtnetback.Services.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoController extends BaseControllerImpl <Grupo, GrupoServiceImpl> {
    private final GrupoService service;

    @GetMapping("/obtenerGrupos")
    public Page<DTOGrupoSimple> obtenerGrupos(
            @RequestParam String texto,
            @RequestParam int page) {
        return service.obtenerGrupos(texto, page);
    }
}
