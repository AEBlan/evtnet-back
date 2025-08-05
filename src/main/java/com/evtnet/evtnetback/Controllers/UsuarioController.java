package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioRepository repository;

    @GetMapping
    public List<Usuario> ListarUsuarios() {
        return repository.findAll();
    }
    
}
