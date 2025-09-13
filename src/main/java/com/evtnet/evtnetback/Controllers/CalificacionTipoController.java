package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.CalificacionTipo;
import com.evtnet.evtnetback.Services.CalificacionTipoServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/calificacionTipos")
@RequiredArgsConstructor
public class CalificacionTipoController extends BaseControllerImpl<CalificacionTipo, CalificacionTipoServiceImpl> {
    
}

