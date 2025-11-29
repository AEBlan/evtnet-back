package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.chat.DTOChatResponse;
import com.evtnet.evtnetback.dto.mensaje.DTOMensajeResponse;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.EspacioRepository;
import com.evtnet.evtnetback.service.ChatService;
import com.evtnet.evtnetback.service.MensajeService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MensajeService mensajeService;

    @GetMapping("/{chatId}/mensajes")
    public List<DTOMensajeResponse> obtenerMensajesDeChat(@PathVariable Long chatId) {
        return mensajeService.obtenerHistorial(chatId);
    }

    @PostMapping("/directo")
    public DTOChatResponse crearChatDirecto(@RequestParam String username) throws Exception {
        return chatService.crearChatDirecto(username);
    }

    @GetMapping("/detalle/{idChat}")
    public DTOChatResponse obtenerChat (@PathVariable Long idChat) throws Exception {
        return chatService.obtenerChat(idChat);
    }

    @GetMapping("/obtenerDirectos")
    public List<DTOChatResponse> obtenerDirectos () throws Exception {
        return chatService.obtenerDirectos();
    }
    @GetMapping("/buscarChats")
    public List<DTOChatResponse> buscarChats(@RequestParam String texto) throws Exception {
        return chatService.buscarChats(texto);
    }

    @PostMapping("/enviarMensaje/{idChat}")
    public void enviarMensaje (@PathVariable Long idChat, @RequestParam String mensaje) throws Exception {
        mensajeService.enviarMensaje(idChat, mensaje);
    }
    
}