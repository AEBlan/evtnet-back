package com.evtnet.evtnetback.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evtnet.evtnetback.Entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
