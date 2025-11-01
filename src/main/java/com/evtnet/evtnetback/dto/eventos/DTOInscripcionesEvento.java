package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class DTOInscripcionesEvento {
        private String nombreEvento;
        private boolean esAdministrador;
        private boolean esOrganizador;
        private boolean esEncargado;
        private List<InscripcionDTO> inscripciones;

    @Builder
    @Data
    public static class InscripcionDTO {
        private Long id;
        private UsuarioDTO usuario;
        private LocalDateTime fechaInscripcion;
        private LocalDateTime fechaCancelacionInscripcion;
        private List<TransferenciaDTO> transferencias;
        private List<InvitadoDTO> invitados;
    }

    @Builder
    @Data
    public static class UsuarioDTO {
        private String username;
        private String nombre;
        private String apellido;
        private String dni;
    }

    @Builder
    @Data
    public static class TransferenciaDTO {
        private String numero;
        private BigDecimal monto;
    }

    @Builder
    @Data
    public static class InvitadoDTO {
        private String nombre;
        private String apellido;
        private String dni;
    }
}


