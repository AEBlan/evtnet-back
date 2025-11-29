package com.evtnet.evtnetback.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.evtnet.evtnetback.entity.Chat;

public interface ChatRepository extends BaseRepository <Chat, Long> {

    // ✅ Para listar directos de un usuario (6 args)
    List<Chat> findAllByTipoAndUsuario1_UsernameOrTipoAndUsuario2_Username(
            Chat.Tipo t1, String username1,
            Chat.Tipo t2, String username2);

    // ✅ Para listar por ámbito
    List<Chat> findAllByTipoAndEvento_Id(Chat.Tipo tipo, Long eventoId);
    List<Chat> findAllByTipoAndSuperEvento_Id(Chat.Tipo tipo, Long superEventoId);


    // Buscar chat DIRECTO entre dos usernames, sin importar el orden
        @Query("""
        SELECT c FROM Chat c
        WHERE c.tipo = 'DIRECTO'
          AND ((c.usuario1.username = :u1 AND c.usuario2.username = :u2)
            OR (c.usuario1.username = :u2 AND c.usuario2.username = :u1))
    """)
    Optional<Chat> findDirectoBetween(String u1, String u2);


    @Query("""
        SELECT c FROM Chat c
        WHERE c.tipo = 'DIRECTO'
        AND c.usuario1.username LIKE :username OR c.usuario2.username LIKE :username
            """)
    List<Chat> getDirectos(String username);


    @Query("""
    SELECT DISTINCT c FROM Chat c
    WHERE 
    (c.tipo = 'DIRECTO' AND c.id IN (
        SELECT c2.id FROM Chat c2
        WHERE c2.tipo = 'DIRECTO'
        AND c2.usuario1 IS NOT NULL 
        AND c2.usuario2 IS NOT NULL
        AND (c2.usuario1.username = :username OR c2.usuario2.username = :username)
        AND (
            LOWER(c2.usuario1.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(c2.usuario1.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(c2.usuario1.username) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(c2.usuario2.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(c2.usuario2.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(c2.usuario2.username) LIKE LOWER(CONCAT('%', :texto, '%'))
        )
    )) OR
    (c.tipo = 'EVENTO' AND c.id IN (
        SELECT c2.id FROM Chat c2
        JOIN c2.evento evt
        LEFT JOIN evt.inscripciones ins
        LEFT JOIN evt.administradoresEvento admEvt
        WHERE c2.tipo = 'EVENTO'
        AND (
            (ins.fechaHoraBaja IS NULL AND ins.usuario.username = :username)
            OR (admEvt.fechaHoraBaja IS NULL AND admEvt.usuario.username = :username)
        )
        AND LOWER(evt.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    )) OR
    (c.tipo = 'SUPEREVENTO' AND c.id IN (
        SELECT c2.id FROM Chat c2
        JOIN c2.superEvento supEvt
        JOIN supEvt.administradorSuperEventos admSup
        WHERE c2.tipo = 'SUPEREVENTO'
        AND admSup.fechaHoraBaja IS NULL 
        AND admSup.usuario.username = :username
        AND LOWER(supEvt.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    )) OR
    (c.tipo = 'ESPACIO' AND c.id IN (
        SELECT c2.id FROM Chat c2
        JOIN c2.espacio esp
        JOIN esp.administradoresEspacio admEsp
        WHERE c2.tipo = 'ESPACIO'
        AND admEsp.fechaHoraBaja IS NULL 
        AND admEsp.usuario.username = :username
        AND LOWER(esp.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    )) OR
    (c.tipo = 'GRUPAL' AND c.id IN (
        SELECT c2.id FROM Chat c2
        JOIN c2.grupo grp
        JOIN grp.usuariosGrupo usrGrp
        WHERE c2.tipo = 'GRUPAL'
        AND usrGrp.fechaHoraBaja IS NULL 
        AND usrGrp.aceptado = true
        AND usrGrp.usuario.username = :username
        AND LOWER(grp.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
    ))
    """)
    List<Chat> buscar(String username, String texto);

    // ESPACIO
    List<Chat> findAllByTipoAndEspacio_Id(Chat.Tipo tipo, Long espacioId);

    // Para obtener el chat único de un espacio (único chat por espacio)
    Optional<Chat> findByEspacio_Id(Long espacioId);

    // EVENTO

    List<Chat> findAllByEvento_Id(Long eventoId);

    // Para obtener el chat único de un evento (único chat por evento)
    Optional<Chat> findByEvento_Id(Long eventoId);


    //SUPEREVENTO

    Optional<Chat> findBySuperEvento_Id(Long superEventoId);






}
