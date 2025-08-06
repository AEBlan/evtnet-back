package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Cliente;
import com.evtnet.evtnetback.Services.ClienteServiceImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController extends BaseControllerImpl<Cliente, ClienteServiceImpl> {
}
