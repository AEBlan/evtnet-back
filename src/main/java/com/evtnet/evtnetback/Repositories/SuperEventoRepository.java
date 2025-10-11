package com.evtnet.evtnetback.Repositories;

import java.util.List;

import com.evtnet.evtnetback.Entities.SuperEvento;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface SuperEventoRepository extends BaseRepository <SuperEvento, Long> {
    @Query("""
        SELECT DISTINCT s
        FROM SuperEvento s
        JOIN s.administradorSuperEventos a
        JOIN a.usuario u
        WHERE u.username = :username
        AND a.fechaHoraBaja IS NULL
    """)
    List<SuperEvento> findAllByUsuario_Username(
            @Param("username") String username);

    @Query("""
        SELECT DISTINCT s
        FROM SuperEvento s
        JOIN s.administradorSuperEventos a
        JOIN a.usuario u
        WHERE u.username = :username
        AND a.fechaHoraBaja IS NULL
        AND (
            s.nombre LIKE %:search%
            OR s.descripcion LIKE %:search%
        )
    """)
    List<SuperEvento> searchByUsuario_Username(
            @Param("username") String username, @Param("search") String search);
}
