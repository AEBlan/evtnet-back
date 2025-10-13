package com.evtnet.evtnetback.dto.supereventos;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOAdministradoresSuperevento {
    private Boolean esOrganizador;
    private String nombreSuperevento;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DTOAdministradores { 
        private String nombre;
        private String apellido;
        private String username;

        private Boolean vigente;

        @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
        public static class DTOHistorico {
            private Long fechaDesde;
            private Long fechaHasta;
            private Boolean organizador;
        }

        private List<DTOHistorico> historico;
    }

    private List<DTOAdministradores> administradores;
}
