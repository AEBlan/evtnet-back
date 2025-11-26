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
