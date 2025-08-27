package com.evtnet.evtnetback.dto.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEnviarCodigoRecuperarContrasena {
    @NotBlank
    @Email
    private String mail;
}