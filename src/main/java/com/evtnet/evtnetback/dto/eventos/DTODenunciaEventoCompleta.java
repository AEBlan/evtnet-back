package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTODenunciaEventoCompleta {
    private Long id;
    private String titulo;
    private String descripcion;
    private DenuncianteDTO denunciante;
    private List<HistoricoDTO> historico;
    private EventoDTO evento;
    private boolean permiteCambioEstado;

    @Builder @Data
    public static class DenuncianteDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }

    @Builder @Data
    public static class HistoricoDTO {
        private String nombre;
        private LocalDateTime fechaHoraDesde;
        private String descripcion;
        private ResponsableDTO responsable;
    }

    @Builder @Data
    public static class ResponsableDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }

    @Builder @Data
    public static class EventoDTO {
        private Long id;
        private String nombre;
        private String descripcion;
        private EspacioDTO espacio;
        private LocalDateTime fechaHoraInicio;
        private LocalDateTime fechaHoraFin;
        private int participantes;
        private OrganizadorDTO organizador;
        private List<AdministradorDTO> administradores;
    }

    @Builder @Data
    public static class EspacioDTO {
        private String nombre; // puede ser null
        private String direccion;
    }

    @Builder @Data
    public static class OrganizadorDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }

    @Builder @Data
    public static class AdministradorDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }
}
