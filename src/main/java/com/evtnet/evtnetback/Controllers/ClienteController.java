package com.evtnet.evtnetback.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.evtnet.evtnetback.Entities.Cliente;
import com.evtnet.evtnetback.Repositories.ClienteRepository;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository repository;

    @GetMapping
    public List<Cliente> listarClientes() {
        return repository.findAll();
    }
}
