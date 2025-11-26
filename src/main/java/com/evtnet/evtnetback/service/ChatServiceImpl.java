package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.chat.DTOChatResponse;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl extends BaseServiceImpl <Chat, Long> implements ChatService {

    private final ChatRepository chatRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;
    private final EventoRepository eventoRepository;
    private final SuperEventoRepository superEventoRepository;

    public ChatServiceImpl(
        ChatRepository chatRepository, 
        UsuarioRepository usuarioRepository, 
        EspacioRepository espacioRepository,
        EventoRepository eventoRepository,
        SuperEventoRepository superEventoRepository) {
        super(chatRepository);
        this.chatRepository = chatRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
        this.eventoRepository = eventoRepository;
        this.superEventoRepository = superEventoRepository;

    }

    @Override
    public List<Chat> findAllByUsuario(String username) {
        return chatRepository.findAllByTipoAndUsuario1_UsernameOrTipoAndUsuario2_Username(
                Chat.Tipo.DIRECTO, username,
                Chat.Tipo.DIRECTO, username
        );
    }

    @Override
    public List<Chat> findAllBySuperEvento(Long superEventoId) {
        return chatRepository.findAllByTipoAndSuperEvento_Id(Chat.Tipo.SUPEREVENTO, superEventoId);
    }

    @Override
    public Optional<Chat> findDirectoBetween(String username1, String username2) {
        return chatRepository.findDirectoBetween(username1, username2);
    }

    @Override
    public Chat crearChatDirecto(String username1, String username2) {

        // validar que no exista
        Optional<Chat> existente = chatRepository.findDirectoBetween(username1, username2);
        if (existente.isPresent()) {
            return existente.get();
        }

        Usuario u1 = usuarioRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username1));

        Usuario u2 = usuarioRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username2));

        Chat nuevo = Chat.builder()
                .tipo(Chat.Tipo.DIRECTO)
                .usuario1(u1)
                .usuario2(u2)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        return chatRepository.save(nuevo);
    }

    //Espacio

    @Override
    public List<Chat> findAllByEspacio(Long espacioId) {
        return chatRepository.findAllByTipoAndEspacio_Id(Chat.Tipo.ESPACIO, espacioId);
    }

    @Override
    public Optional<Chat> findByEspacioId(Long espacioId) {
        return chatRepository.findByEspacio_Id(espacioId);
    }

    @Override
    public List<Chat> findByTipoAndEspacio_Id(Chat.Tipo tipo, Long espacioId) {
        return chatRepository.findAllByTipoAndEspacio_Id(tipo, espacioId);
    }
    
    @Override
    @Transactional
    public DTOChatResponse getOrCreateChatParaEspacio(Long espacioId) {
        Chat chat = chatRepository.findByEspacio_Id(espacioId)
                .orElseGet(() -> crearChatParaEspacio(espacioId));

        // acá convertimos la entidad al DTOResponse
        return toChatResponseDTO(chat);
    }

    private Chat crearChatParaEspacio(Long espacioId) {
        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new RuntimeException("Espacio no encontrado"));

        Chat chat = Chat.builder()
                .tipo(Chat.Tipo.ESPACIO)
                .espacio(espacio)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    // 🔹 Mapeo manual a DTO 
    private DTOChatResponse toChatResponseDTO(Chat chat) {
        DTOChatResponse dto = new DTOChatResponse();
        dto.setId(chat.getId());
        dto.setTipo(chat.getTipo().name());
        dto.setFechaHoraAlta(chat.getFechaHoraAlta());

        // Espacio
        if (chat.getEspacio() != null) {
            dto.setEspacioId(chat.getEspacio().getId());
            dto.setNombreEspacio(chat.getEspacio().getNombre());
        }

        // Evento
        if (chat.getEvento() != null) {
            dto.setEventoId(chat.getEvento().getId());
            dto.setNombreEvento(chat.getEvento().getNombre());
        }

        // SuperEvento (Faltaba 🚀)
        if (chat.getSuperEvento() != null) {
            dto.setSuperEventoId(chat.getSuperEvento().getId());
            dto.setNombreSuperEvento(chat.getSuperEvento().getNombre());
        }

        return dto;
    }


    
    // Para evento
    @Override
    public List<Chat> findAllByEvento(Long eventoId) {
        return chatRepository.findAllByTipoAndEvento_Id(Chat.Tipo.EVENTO, eventoId);
    }

    @Override
    @Transactional
    public DTOChatResponse getOrCreateChatParaEvento(Long eventoId) {
        Chat chat = chatRepository.findByEvento_Id(eventoId)
                .orElseGet(() -> crearChatParaEvento(eventoId));

        return toChatResponseDTO(chat);
    }
    private Chat crearChatParaEvento(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        Chat chat = Chat.builder()
                .tipo(Chat.Tipo.EVENTO)
                .evento(evento)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    // Para super evento
    @Override
    @Transactional
    public DTOChatResponse getOrCreateChatParaSuperEvento(Long superEventoId) {

        Chat chat = chatRepository.findBySuperEvento_Id(superEventoId)
                .orElseGet(() -> crearChatParaSuperEvento(superEventoId));

        return toChatResponseDTO(chat);
    }

    private Chat crearChatParaSuperEvento(Long superEventoId) {

        SuperEvento superEvento = superEventoRepository.findById(superEventoId)
                .orElseThrow(() -> new RuntimeException("SuperEvento no encontrado"));

        Chat chat = Chat.builder()
                .tipo(Chat.Tipo.SUPEREVENTO)
                .superEvento(superEvento)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    
}
