package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor 
public class DTOAuth {
    private String token;
    private List<String> permisos;
    private String username;
    private boolean vinculadoMP;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class User { private String nombre; private String apellido; private List<String> roles; }

    private User user;
}