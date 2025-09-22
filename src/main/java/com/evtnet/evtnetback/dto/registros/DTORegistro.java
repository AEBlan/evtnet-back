package com.evtnet.evtnetback.dto.registros;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
public class DTORegistro {
    private String tipo;
    private String subtipo;
    private long fechaHora;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Usuario { private String nombre; private String apellido; private String username; }

    private Usuario usuario;

    private String solicitud;
    private String descripcion;
}

