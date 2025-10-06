package com.evtnet.evtnetback.dto.usuarios;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DTODenunciaUsuario {
    private LocalDateTime fecha;     // TS: Date
    private String descripcion;
    private Persona denunciado;      // organizador (denunciado)
    private Persona denunciante;     // quien denuncia

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Persona {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }

    // Constructor que usa la query JPQL del repo
    public DTODenunciaUsuario(
            LocalDateTime fecha, String descripcion,
            String denunciadoNombre, String denunciadoApellido, String denunciadoUsername, String denunciadoMail,
            String denuncianteNombre, String denuncianteApellido, String denuncianteUsername, String denuncianteMail
    ) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.denunciado  = new Persona(denunciadoNombre,  denunciadoApellido,  denunciadoUsername,  denunciadoMail);
        this.denunciante = new Persona(denuncianteNombre, denuncianteApellido, denuncianteUsername, denuncianteMail);
    }
}
