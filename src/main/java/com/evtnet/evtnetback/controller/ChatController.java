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

    // ===================================================
    // 1) OBTENER TODOS LOS CHATS DE UN USUARIO
    // ===================================================
    @GetMapping("/usuario/{username}")
    public List<Chat> obtenerChatsDeUsuario(@PathVariable String username) {
        return chatService.findAllByUsuario(username);
    }

    // ===================================================
    // 2) OBTENER UN CHAT DIRECTO ENTRE DOS USUARIOS
    // ===================================================
    @GetMapping("/directo")
    public Chat obtenerChatDirecto(
            @RequestParam String u1,
            @RequestParam String u2
    ) {
        return chatService.findDirectoBetween(u1, u2)
                .orElseThrow(() -> new RuntimeException("No existe chat directo entre " + u1 + " y " + u2));
    }

    // ===================================================
    // 3) HISTORIAL DE MENSAJES DE UN CHAT
    // ===================================================
    @GetMapping("/{chatId}/mensajes")
    public List<DTOMensajeResponse> obtenerMensajesDeChat(@PathVariable Long chatId) {
        return mensajeService.obtenerHistorial(chatId);
    }

    // ===================================================
    // 4) CREAR CHAT DIRECTO (si no existe)
    // ===================================================
    @PostMapping("/directo")
    public Chat crearChatDirecto(
            @RequestParam String u1,
            @RequestParam String u2
    ) {
        return chatService.findDirectoBetween(u1, u2)
                .orElseGet(() -> chatService.crearChatDirecto(u1, u2));
    }

    // ===================================================
    //  CREAR CHAT ESPACIO (si no existe)
    // ===================================================
    @GetMapping("/espacio/{espacioId}")
    public ResponseEntity<DTOChatResponse> getOrCreateChat(@PathVariable Long id) {
        DTOChatResponse dto = chatService.getOrCreateChatParaEspacio(id);
        return ResponseEntity.ok(dto);
    }

    // ================================================================
    // Obtener / crear chat de EVENTO
    // ================================================================
    @GetMapping("/evento/{eventoId}")
    public DTOChatResponse crearChatParaEvento(@PathVariable Long eventoId) {
        return chatService.getOrCreateChatParaEvento(eventoId);
    }

    // ================================================================
    // Obtener / crear chat de SUPEREVENTO
    // ================================================================

    @GetMapping("/superevento/{superEventoId}")
    public DTOChatResponse crearChatParaSuperEvento(@PathVariable Long superEventoId) {
        return chatService.getOrCreateChatParaSuperEvento(superEventoId);
    }
    
}