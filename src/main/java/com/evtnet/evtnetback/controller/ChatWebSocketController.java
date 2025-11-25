package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.mensaje.DTOMensaje;
import com.evtnet.evtnetback.dto.mensaje.DTOMensajeResponse;
import com.evtnet.evtnetback.entity.Chat;
import com.evtnet.evtnetback.entity.Mensaje;
import com.evtnet.evtnetback.entity.Usuario;
import com.evtnet.evtnetback.service.ChatService;
import com.evtnet.evtnetback.service.MensajeService;
import com.evtnet.evtnetback.repository.ChatRepository;
import com.evtnet.evtnetback.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController  {

    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final MensajeService mensajeService;
    private final ChatRepository chatRepo;
    private final UsuarioRepository usuarioRepo;

    @MessageMapping("/chat.send")
    public void send(DTOMensaje dto) {
    
        try {

                Chat chat = chatRepo.findById(dto.getChatId())
                    .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

                Usuario autor = usuarioRepo.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Mensaje msg = Mensaje.builder()
                        .chat(chat)
                        .usuario(autor)
                        .texto(dto.getTexto())
                        .fechaHora(LocalDateTime.now())
                        .build();

                Mensaje saved = mensajeService.save(msg);

                DTOMensajeResponse response = DTOMensajeResponse.builder()
                        .id(saved.getId())
                        .chatId(chat.getId())
                        .usuarioId(autor.getId())
                        .usuarioNombre(autor.getNombre())
                        .texto(saved.getTexto())
                        .fechaHora(saved.getFechaHora())
                        .build();

                template.convertAndSend("/topic/chat/" + chat.getId(), response);
            
            } catch (Exception e) {
            e.printStackTrace();
            // Podés enviar un mensaje de error si querés
            }
    }

}
