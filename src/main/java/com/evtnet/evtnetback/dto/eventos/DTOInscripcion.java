package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.usuarios.DTOPago;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOInscripcion {
    private Long idEvento;
    private String username;
    private List<Invitado> invitados;
    private BigDecimal precioInscripcion;
    private List<DTOPago> datosPago; // puede ser null

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Invitado { private String nombre; private String apellido; private String dni; }
}

