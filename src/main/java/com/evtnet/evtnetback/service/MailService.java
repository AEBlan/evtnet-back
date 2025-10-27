package com.evtnet.evtnetback.service;


public interface MailService {
    void enviar(String to, String subject, String body);
    
}
