package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface GrupoRepository extends BaseRepository <Grupo, Long> {
    @Query("SELECT g FROM Grupo g " +
           "WHERE LOWER(g.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "   OR LOWER(g.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Grupo> buscarPorTexto(String texto, Pageable pageable);
}
