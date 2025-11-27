package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.chat.DTOChatResponse;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.util.CurrentUser;
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
    public DTOChatResponse crearChatDirecto(String username) throws Exception {

        String usernameOrigen = CurrentUser.getUsername().orElseThrow(() -> new Exception("No se encontró al usuario"));

        // validar que no exista
        Optional<Chat> existente = chatRepository.findDirectoBetween(usernameOrigen, username);
        if (existente.isPresent()) {
            return toChatResponseDTO(existente.get());
        }

        Usuario u1 = usuarioRepository.findByUsername(usernameOrigen)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usernameOrigen));

        Usuario u2 = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        Chat nuevo = Chat.builder()
                .tipo(Chat.Tipo.DIRECTO)
                .usuario1(u1)
                .usuario2(u2)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        return toChatResponseDTO(chatRepository.save(nuevo));
    }


    public DTOChatResponse obtenerChat(Long idChat) throws Exception {
        Chat chat = chatRepository.findById(idChat).orElseThrow(() -> new Exception("No se encontró el chat"));
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No se encontró al usuario"));

        validarAcceso(chat, username);

        return toChatResponseDTO(chat);
    }

    



    private DTOChatResponse toChatResponseDTO(Chat chat) throws Exception {
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

        // Usuario
        if (chat.getUsuario1() != null && chat.getUsuario2() != null) {
            String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Usuario no encontrado"));

            if (chat.getUsuario1().getUsername().equals(username)) {
                dto.setUsuarioUsername(chat.getUsuario2().getUsername());
                dto.setUsuarioNombre(chat.getUsuario2().getNombre());
                dto.setUsuarioApellido(chat.getUsuario2().getApellido());
            } else {
                dto.setUsuarioUsername(chat.getUsuario1().getUsername());
                dto.setUsuarioNombre(chat.getUsuario1().getNombre());
                dto.setUsuarioApellido(chat.getUsuario1().getApellido());
            }
        }

        // Grupo

        if (chat.getGrupo() != null) {
            dto.setGrupoId(chat.getGrupo().getId());
            dto.setNombreGrupo(chat.getGrupo().getNombre());
        }

        return dto;
    }


    private void validarAcceso(Chat chat, String username) throws Exception {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("No se encontró al usuario"));

        switch (chat.getTipo()) {

            case DIRECTO -> {
                if (chat.getUsuario1() == null || chat.getUsuario2() == null) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
                if (!chat.getUsuario1().getUsername().equals(username) && chat.getUsuario2().getUsername().equals(username)) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
            }
            case EVENTO -> {
                if (chat.getEvento() == null) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
                boolean inscripto = chat.getEvento().getInscripciones().stream().filter(ins -> ins.getFechaHoraBaja() == null).map(ins -> ins.getUsuario().getUsername()).toList().contains(username);
                boolean admin = chat.getEvento().getAdministradoresEvento().stream().filter(adm -> adm.getFechaHoraBaja() == null).map(adm -> adm.getUsuario().getUsername()).toList().contains(username);
                if (!inscripto && !admin) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
            }
            case SUPEREVENTO -> {
                if (chat.getSuperEvento() == null) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
                boolean admin = chat.getSuperEvento().getAdministradorSuperEventos().stream().filter(adm -> adm.getFechaHoraBaja() == null).map(adm -> adm.getUsuario().getUsername()).toList().contains(username);
                if (!admin) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
            }
            case ESPACIO -> {
                if (chat.getEspacio() == null) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
                boolean admin = chat.getEspacio().getAdministradoresEspacio().stream().filter(adm -> adm.getFechaHoraBaja() == null).map(adm -> adm.getUsuario().getUsername()).toList().contains(username);
                if (!admin) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
            }
            case GRUPAL -> {
                if (chat.getGrupo() == null) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
                boolean admin = chat.getGrupo().getUsuariosGrupo().stream().filter(usr -> usr.getFechaHoraBaja() == null).map(usr -> usr.getUsuario().getUsername()).toList().contains(username);
                if (!admin) {
                    throw new Exception("No tiene permiso para acceder a este chat");
                }
            }
        }
    }

    
}
