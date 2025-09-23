package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTODenunciaEventoSimple {
    private Long idDenuncia;
    private String titulo;
    private String usernameDenunciante;
    private String nombreEvento;
    private String usernameOrganizador;
    private String estado;
    private LocalDateTime fechaHoraUltimoCambio;
    private LocalDateTime fechaHoraIngreso;
}
