package com.evtnet.evtnetback.dto.usuarios;

import java.util.List;

public record DTOAuth(
    String token,
    List<String> permisos,
    String username
) {}

