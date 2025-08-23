package com.evtnet.evtnetback.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        // Endpoints públicos que no requieren token
        if (isPublicEndpoint(request.getRequestURI(), request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            try {
                String token = auth.substring(7);
                if (jwtUtil.validate(token)) {
                    String username = jwtUtil.getUsername(token);
                    var authToken = new UsernamePasswordAuthenticationToken(
                            username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ignored) {
                // Si hay error, seguimos sin autenticación; SecurityConfig decide acceso.
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestURI, String method) {
        return (method.equals("POST") && (
                requestURI.equals("/usuarios/registrarse") ||
                requestURI.equals("/usuarios/iniciarSesion") ||
                requestURI.equals("/usuarios/ingresarCodigo")||
                requestURI.equals("/usuarios/recuperarContrasena")
        )) || (method.equals("PUT") && (
                requestURI.equals("/usuarios/enviarCodigo") ||
                requestURI.equals("/usuarios/enviarCodigoRecuperarContrasena")
        )) || (method.equals("GET") && (
                requestURI.equals("/usuarios/obtenerImagenDeCalificacion") ||
                requestURI.equals("/usuarios/verificarUsernameDisponible")
        ));
    }
}