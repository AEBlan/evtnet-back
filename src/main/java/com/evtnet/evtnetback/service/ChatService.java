package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Chat;
import com.evtnet.evtnetback.dto.chat.*;

import java.util.Optional;
import java.util.List;

public interface ChatService extends BaseService <Chat, Long> {
    List<Chat> findAllByUsuario(String username);

    Optional<Chat> findDirectoBetween(String username1, String username2);

    Chat crearChatDirecto(String username1, String username2);

    // Para espacio

    List<Chat> findAllByEspacio(Long espacioId);

    Optional<Chat> findByEspacioId(Long espacioId);

    List<Chat> findByTipoAndEspacio_Id(Chat.Tipo tipo, Long espacioId);

    DTOChatResponse getOrCreateChatParaEspacio(Long espacioId);

    // Para evento

    DTOChatResponse getOrCreateChatParaEvento(Long eventoId);

    List<Chat> findAllByEvento(Long eventoId);

    // Para superevento

    DTOChatResponse getOrCreateChatParaSuperEvento(Long superEventoId);

    List<Chat> findAllBySuperEvento(Long superEventoId);


    
    
}
