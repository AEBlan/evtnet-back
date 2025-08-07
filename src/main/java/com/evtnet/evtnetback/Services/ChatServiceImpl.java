package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Chat;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ChatServiceImpl extends BaseServiceImpl <Chat, Long> implements ChatService {

    public ChatServiceImpl(BaseRepository<Chat, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
