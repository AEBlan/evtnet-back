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
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

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
                    List<String> roles = jwtUtil.getRoles(token); // <-- método agregado en JwtUtil

                    var authorities = roles.stream()
                            .map(r -> new SimpleGrantedAuthority(toSpringRole(r)))
                            .collect(Collectors.toList());

                    var authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
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
                requestURI.equals("/usuarios/ingresarCodigo") ||
                requestURI.equals("/usuarios/loginGoogle") ||
                requestURI.equals("/usuarios/recuperarContrasena")
        )) || (method.equals("PUT") && (
                requestURI.equals("/usuarios/enviarCodigo") ||
                requestURI.equals("/usuarios/definirContrasena") ||
                requestURI.equals("/usuarios/enviarCodigoRecuperarContrasena")
        )) || (method.equals("GET") && (
                requestURI.equals("/usuarios/obtenerImagenDeCalificacion") ||
                requestURI.equals("/usuarios/verificarUsernameDisponible")
        ));
    }

    // ==== AQUÍ EL MÉTODO QUE NORMALIZA LOS NOMBRES DE ROL A "ROLE_*" ====
    private String toSpringRole(String r) {
        if (r == null || r.isBlank()) return "ROLE_USER";
        // 1) quitar tildes
        String base = Normalizer.normalize(r, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // 2) reemplazar no alfanumérico por "_"
        base = base.replaceAll("[^A-Za-z0-9]+", "_");
        // 3) mayúsculas
        base = base.toUpperCase();
        // 4) prefijo ROLE_
        return "ROLE_" + base; // Usuario -> ROLE_USUARIO; PendienteConfirmacion -> ROLE_PENDIENTECONFIRMACION
    }
}
