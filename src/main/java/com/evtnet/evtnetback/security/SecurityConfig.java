package com.evtnet.evtnetback.security;

import lombok.RequiredArgsConstructor;

import org.springframework.security.config.Customizer;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
            // CORS preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // ===== Eventos (públicos SOLO para pruebas) =====
            .requestMatchers(HttpMethod.GET,
                "/eventos/obtenerEvento",
                "/eventos/obtenerDatosCreacionEvento",
                "/eventos/obtenerCantidadEventosSuperpuestos",
                "/eventos/obtenerEventoParaInscripcion",
                "/eventos/obtenerMontoDevolucionCancelacionInscripcion",
                "/eventos/obtenerDatosModificacionEvento"
            ).permitAll()
            .requestMatchers(HttpMethod.PUT,
                "/eventos/buscar",
                "/eventos/buscarMisEventos",
                "/eventos/verificarDatosPrePago"
            ).permitAll()
            .requestMatchers(HttpMethod.POST,
                "/eventos/crearEvento",
                "/eventos/inscribirse",
                "/eventos/desinscribirse",
                "/eventos/modificarEvento"
            ).permitAll()

            // ===== Endpoints de usuarios públicos que ya tenías (compactado) =====
            .requestMatchers(HttpMethod.POST,
                "/usuarios/registrarse",
                "/usuarios/iniciarSesion",
                "/usuarios/ingresarCodigo",
                "/usuarios/enviarCodigoRecuperarContrasena",
                "/usuarios/loginGoogle",
                "/usuarios/registrarseConFoto"
            ).permitAll()
            .requestMatchers(HttpMethod.PUT,
                "/usuarios/enviarCodigo",
                "/usuarios/definirContrasena",
                "/usuarios/recuperarContrasena"
            ).permitAll()
            .requestMatchers(HttpMethod.GET,
                "/usuarios/obtenerImagenDeCalificacion",
                "/usuarios/verificarUsernameDisponible"
            ).permitAll()

            // Archivos/imagenes públicas de prueba
            .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/imagenes-espacio/espacios/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/imagenes-espacio/espacios/*/upload").permitAll()
            .requestMatchers(HttpMethod.POST, "/iconos-caracteristica/caracteristicas/*/upload").permitAll()

            // Todo lo demás, autenticado
            .anyRequest().authenticated()
        )

                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), BasicAuthenticationFilter.class);

            return http.build();
        }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
