package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mensaje.DTOMensajeResponse;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensajeServiceImpl extends BaseServiceImpl <Mensaje, Long> implements MensajeService {
    private final MensajeRepository mensajeRepository;
    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;

    public MensajeServiceImpl(MensajeRepository mensajeRepository,
                              ChatRepository chatRepository,
                              UsuarioRepository usuarioRepository) {
        super(mensajeRepository);           // importante: super primero
        this.mensajeRepository = mensajeRepository;
        this.chatRepository = chatRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Mensaje enviarMensaje(Long chatId, Long usuarioAutorId, String texto) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado: " + chatId));

        Usuario autor = usuarioRepository.findById(usuarioAutorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioAutorId));

        Mensaje mensaje = Mensaje.builder()
                .chat(chat)
                .usuario(autor)
                .texto(texto)
                .fechaHora(LocalDateTime.now())
                .build();

        return mensajeRepository.save(mensaje);
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
                .chatId(mensaje.getChat().getId())
                .usuarioId(mensaje.getUsuario().getId())
                .usuarioNombre(mensaje.getUsuario().getNombre())
                .texto(mensaje.getTexto())
                .fechaHora(mensaje.getFechaHora())
                .build();
    }
}
