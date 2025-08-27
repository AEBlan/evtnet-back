package com.evtnet.evtnetback.Services;


public interface MailService {
    void enviar(String to, String subject, String body);
    
}
