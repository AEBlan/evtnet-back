package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Chat;
import com.evtnet.evtnetback.dto.chat.*;

import java.util.Optional;
import java.util.List;

public interface ChatService extends BaseService <Chat, Long> {
    DTOChatResponse crearChatDirecto(String username) throws Exception;

    DTOChatResponse obtenerChat(Long idChat) throws Exception;
}
