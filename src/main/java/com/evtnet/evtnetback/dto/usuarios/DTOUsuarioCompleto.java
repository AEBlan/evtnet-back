// DTOUsuarioCompleto.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOUsuarioCompleto {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;
    private String dni;
    private Long   fechaNacimiento; // epoch ms
    private Long   alta;            // epoch ms
    private Long   baja;            // epoch ms o null
    private List<String> roles;     // nombres de roles vigentes (sin baja)
}
