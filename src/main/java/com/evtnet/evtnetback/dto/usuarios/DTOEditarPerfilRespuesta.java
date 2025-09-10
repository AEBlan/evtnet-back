// src/main/java/com/evtnet/evtnetback/dto/usuarios/DTOEditarPerfilRespuesta.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOEditarPerfilRespuesta {
    private String nombre;
    private String apellido;
    private String dni;
    private String cbu;
    private Long   fechaNacimiento;

    // Foto opcional (base64 + contentType)
    private String fotoBase64;      // null si no hay
    private String fotoContentType; // ej. "image/png"
}
