package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.Services.UsuarioServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseControllerImpl <Usuario, UsuarioServiceImpl> {
    
    @Autowired
    private UsuarioRepository repository;

    @GetMapping("/todos")
    public List<Usuario> getTodosUsuarios() {
        return repository.findAll();
    }
    
}
