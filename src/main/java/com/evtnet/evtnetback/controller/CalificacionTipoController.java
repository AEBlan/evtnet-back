package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.CalificacionTipo;
import com.evtnet.evtnetback.service.CalificacionTipoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/calificacionTipos")
@RequiredArgsConstructor
public class CalificacionTipoController extends BaseControllerImpl<CalificacionTipo, CalificacionTipoServiceImpl> {
    
}

