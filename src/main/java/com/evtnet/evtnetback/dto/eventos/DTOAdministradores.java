package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOAdministradores {
    private boolean esOrganizador;
    private String nombreEvento;
    private List<AdministradorDTO> administradores;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdministradorDTO {
        private String username;
        private String nombre;
        private String apellido;
        private boolean vigente;
        private List<HistoricoDTO> historico;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoricoDTO {
        private LocalDateTime fechaDesde;
        private LocalDateTime fechaHasta; // null si sigue vigente
    }
}
