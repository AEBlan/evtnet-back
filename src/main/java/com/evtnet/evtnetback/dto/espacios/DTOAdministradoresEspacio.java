package com.evtnet.evtnetback.dto.espacios;

import com.evtnet.evtnetback.dto.eventos.DTOAdministradores;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOAdministradoresEspacio {
    private boolean esPropietario;
    private List<DTOAdministradores> dtoAdministradores;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DTOAdministradores {
        private String nombreApellido;
        private String username;
        private String urlFotoPerfil;
        private String contentType;
        private boolean esPropietario;
        private List<HistoricoDTO> fechasAdministracion;

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class HistoricoDTO {
            private Long fechaDesde;
            private Long fechaHasta;
        }
    }
}
