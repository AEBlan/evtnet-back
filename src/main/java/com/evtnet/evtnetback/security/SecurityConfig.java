package com.evtnet.evtnetback.security;

import lombok.RequiredArgsConstructor;

import org.springframework.security.config.Customizer;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

                // PÚBLICOS (existentes)
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarse").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/iniciarSesion").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/ingresarCodigo").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/loginGoogle").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/recuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/enviarCodigo").permitAll()
                .requestMatchers(HttpMethod.PUT,  "/usuarios/definirContrasena").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/enviarCodigoRecuperarContrasena").permitAll()
                .requestMatchers(HttpMethod.GET,  "/usuarios/obtenerImagenDeCalificacion").permitAll()
                .requestMatchers(HttpMethod.GET,  "/usuarios/verificarUsernameDisponible").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios/registrarseConFoto").permitAll()


                // ARCHIVOS ESTÁTICOS DE SUBIDAS (públicos)
                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()

                // Imágenes de espacios:
                // GET listar por espacio (público)
                .requestMatchers(HttpMethod.GET, "/imagenes-espacio/espacios/**").permitAll()
                // POST subir imagen (déjalo authenticated en prod; PERMITALL solo si querés probar rápido)
                .requestMatchers(HttpMethod.POST, "/imagenes-espacio/espacios/*/upload").permitAll()

                // Iconos de característica (si querés probar subidas sin token)
                .requestMatchers(HttpMethod.POST, "/iconos-caracteristica/caracteristicas/*/upload").permitAll()

                // EL RESTO AUTENTICADO
                .anyRequest().authenticated()
            )
            .cors(withDefaults())
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
