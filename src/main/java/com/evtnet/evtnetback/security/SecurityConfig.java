package com.evtnet.evtnetback.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .authorizeHttpRequests(auth -> auth
               
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarse").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/iniciarSesion").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/ingresarCodigo").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/loginGoogle").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/recuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT, "/usuarios/enviarCodigo").permitAll()
                .requestMatchers(HttpMethod.PUT, "/usuarios/definirContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT, "/usuarios/enviarCodigoRecuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios/obtenerImagenDeCalificacion").permitAll()
                .requestMatchers(HttpMethod.GET, "/usuarios/verificarUsernameDisponible").permitAll()
                .requestMatchers(HttpMethod.POST, "/eventos/crearEvento").permitAll()
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
