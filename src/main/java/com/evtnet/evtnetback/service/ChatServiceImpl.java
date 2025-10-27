package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Chat;
import com.evtnet.evtnetback.repository.BaseRepository;

public class ChatServiceImpl extends BaseServiceImpl <Chat, Long> implements ChatService {

    public ChatServiceImpl(BaseRepository<Chat, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
