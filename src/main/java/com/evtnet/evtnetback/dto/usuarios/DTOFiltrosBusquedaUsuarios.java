// DTOFiltrosBusquedaUsuarios.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOFiltrosBusquedaUsuarios {
    private String texto;        // busca en nombre, apellido, username, mail, dni
    private List<Long> roles;    // ids de rol: coincide con cualquiera
}

