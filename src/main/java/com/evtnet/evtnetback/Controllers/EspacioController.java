package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Services.EspacioService;
import com.evtnet.evtnetback.Services.EspacioServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/espacios")
public class EspacioController extends BaseControllerImpl <Espacio, EspacioServiceImpl> {

    private final EspacioService espacioService;
    
    public EspacioController(EspacioService service) { 
        this.espacioService = service; 
    }

}

