package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Grupo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GrupoRepository extends BaseRepository <Grupo, Long> {
    @Query("SELECT g FROM Grupo g " +
           "WHERE LOWER(g.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "   OR LOWER(g.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Grupo> buscarPorTexto(String texto, Pageable pageable);
    Optional<Grupo> findById(Long id);

    @Query("SELECT g FROM Grupo g " +
           "JOIN g.usuariosGrupo ug " +
           "WHERE ug.usuario.id = :idUsuario AND ug.fechaHoraBaja IS NULL")
    List<Grupo> findGruposByUsuario(@Param("idUsuario") Long idUsuario);
}
