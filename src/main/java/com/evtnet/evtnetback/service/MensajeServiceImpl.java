package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mensaje.DTOMensajeResponse;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.util.CurrentUser;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeServiceImpl extends BaseServiceImpl <Mensaje, Long> implements MensajeService {
    private final MensajeRepository mensajeRepository;
    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MensajeServiceImpl(MensajeRepository mensajeRepository,
                              ChatRepository chatRepository,
                              UsuarioRepository usuarioRepository,
                              SimpMessagingTemplate simpMessagingTemplate) {
        super(mensajeRepository);           // importante: super primero
        this.mensajeRepository = mensajeRepository;
        this.chatRepository = chatRepository;
        this.usuarioRepository = usuarioRepository;
        this.messagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Mensaje enviarMensaje(Long chatId, String texto) throws Exception {

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Inicie sesión para ver sus chats"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado: " + chatId));

        Usuario autor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: @" + username));

        Mensaje mensaje = Mensaje.builder()
                .chat(chat)
                .usuario(autor)
                .texto(texto)
                .fechaHora(LocalDateTime.now())
                .build();

        mensaje = mensajeRepository.save(mensaje);

        DTOMensajeResponse response = DTOMensajeResponse.builder()
                .id(mensaje.getId())
                .username(mensaje.getUsuario().getUsername())
                .usuarioNombre(mensaje.getUsuario().getNombre())
                .usuarioApellido(mensaje.getUsuario().getApellido())
                .texto(mensaje.getTexto())
                .fechaHora(mensaje.getFechaHora())
                .build();

        messagingTemplate.convertAndSend("/topic/chat/" + mensaje.getChat().getId(), response);

        return mensaje;
    }

    @Override
    public List<DTOMensajeResponse> obtenerHistorial(Long chatId) {

        List<Mensaje> mensajes = mensajeRepository.findByChat_IdOrderByFechaHoraAsc(chatId);

        return mensajes.stream()
                .map(this::toDTOMensajeResponse)
                .toList();
    }

    // Mapping a mano (sin MapStruct)
    private DTOMensajeResponse toDTOMensajeResponse(Mensaje mensaje) {
        return DTOMensajeResponse.builder()
                .id(mensaje.getId())
                .username(mensaje.getUsuario().getUsername())
                .usuarioNombre(mensaje.getUsuario().getNombre())
                .usuarioApellido(mensaje.getUsuario().getApellido())
                .texto(mensaje.getTexto())
                .fechaHora(mensaje.getFechaHora())
                .build();
    }
}
