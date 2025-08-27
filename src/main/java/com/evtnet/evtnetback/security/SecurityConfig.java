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
                // CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // PÚBLICOS (usuarios)
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarse").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/iniciarSesion").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/ingresarCodigo").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/loginGoogle").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/recuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/enviarCodigo").permitAll()
<<<<<<< HEAD
                .requestMatchers(HttpMethod.PUT,  "/usuarios/definirContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/enviarCodigoRecuperarContrasena").permitAll()
=======
                .requestMatchers(HttpMethod.PUT,  "/usuarios/definirContrasena").permitAll() // Solo para ver si funciona 
                .requestMatchers(HttpMethod.POST,  "/usuarios/enviarCodigoRecuperarContrasena").permitAll()
>>>>>>> f7042a77c59b5fb75305ca4c816bf74196a929be
                .requestMatchers(HttpMethod.GET,  "/usuarios/obtenerImagenDeCalificacion").permitAll()
                .requestMatchers(HttpMethod.GET,  "/usuarios/verificarUsernameDisponible").permitAll()

                // EVENTOS: crear requiere usuario confirmado (bloquea PENDIENTE_CONFIRMACION)
                .requestMatchers(HttpMethod.POST, "/eventos/crearEvento").permitAll()

                //.requestMatchers(HttpMethod.POST, "/eventos/crearEvento")
                   //.hasAnyRole("USUARIO","ADMINISTRADOR","SUPERADMINISTRADOR")

                // todo lo demás, autenticado
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
