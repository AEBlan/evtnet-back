package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOEventoParaInscripcion {
    private String nombre;
    private String descripcion;
    private Long idSuperevento;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private Espacio espacio; // null si no registrado
    private String direccion;
    private Ubicacion ubicacion;
    private BigDecimal precioPorAsistente;
    private Integer cantidadMaximaInvitados;
    private Integer limiteParticipantes;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Espacio { private Long id; private String nombre; private String descripcion; }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Ubicacion { private Double latitud; private Double longitud; }
}
