package com.evtnet.evtnetback.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // PÚBLICOS
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarse").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/iniciarSesion").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/ingresarCodigo").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/recuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/enviarCodigo").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/enviarCodigoRecuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.GET,  "/usuarios/obtenerImagenDeCalificacion").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios/verificarUsernameDisponible").permitAll()
                // EL RESTO AUTENTICADO
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
