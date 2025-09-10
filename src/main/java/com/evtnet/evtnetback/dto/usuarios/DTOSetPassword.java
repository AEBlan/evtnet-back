package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;


@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOSetPassword {
    private String mail;
    private String nuevaPassword;
}