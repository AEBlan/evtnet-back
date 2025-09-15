package com.evtnet.evtnetback.Repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.evtnet.evtnetback.Entities.Chat;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface ChatRepository extends BaseRepository <Chat, Long> {

    // ✅ Para listar directos de un usuario (6 args)
    List<Chat> findAllByTipoAndUsuario1_UsernameOrTipoAndUsuario2_Username(
            Chat.Tipo t1, String username1,
            Chat.Tipo t2, String username2);

    // ✅ Para listar por ámbito
    List<Chat> findAllByTipoAndEvento_Id(Chat.Tipo tipo, Long eventoId);
    List<Chat> findAllByTipoAndSuperEvento_Id(Chat.Tipo tipo, Long superEventoId);
    List<Chat> findAllByTipoAndEspacio_Id(Chat.Tipo tipo, Long espacioId);

    // ✅ Buscar un directo entre A y B sin importar el orden
    @Query("""
           select c from Chat c
           where c.tipo = com.evtnet.evtnetback.Entities.Chat$Tipo.DIRECTO
             and ((c.usuario1.username = :u1 and c.usuario2.username = :u2)
               or (c.usuario1.username = :u2 and c.usuario2.username = :u1))
           """)
    Optional<Chat> findDirectoBetween(@Param("u1") String u1, @Param("u2") String u2);
}
