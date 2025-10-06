// DTOResultadoBusquedaUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOResultadoBusquedaUsuario {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;
    private Long   fechaAlta;    // epoch ms (front lo mapea a Date)
    private Long   fechaBaja;    // epoch ms o null
}
