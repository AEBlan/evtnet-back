package com.evtnet.evtnetback.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class CurrentUser {
    private CurrentUser() {}
    public static Optional<String> getUsername() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getName() == null || "anonymousUser".equals(a.getName())) return Optional.empty();
        return Optional.of(a.getName());
    }
}
