package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.evtnet.evtnetback.config.jackson.LocalDateFlexDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOAltaUsuario {
    private String nombre;
    private String apellido;
    private String username;
    private String dni;
    
    @JsonDeserialize(using = LocalDateFlexDeserializer.class)
    private LocalDate fechaNacimiento; // null permitido
    private String mail;
    private List<Long> roles; // IDs de roles
}