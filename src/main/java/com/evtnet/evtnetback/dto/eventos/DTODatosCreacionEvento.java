package com.evtnet.evtnetback.dto.eventos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DTODatosCreacionEvento(
    String nombreEspacio,
    String nombreSubespacio,
    double comisionInscripcion,
    Boolean espacioPublico,
    Boolean requiereAprobarEventos,
    Boolean esAdministradorEspacio,
    Boolean puedeElegirHorarioLibre,
    int diasHaciaAdelante,
    int capacidadMaxima,
    List<String> disciplinasSoportadas,
    List<Cronograma> cronogramas
) {
    public record Cronograma(
        Long id,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta,
        Integer diasAntelacion,
        List<Horario> horarios
    ) {}

    public record Horario(
        String diaSemana,
        LocalTime horaDesde,
        LocalTime horaHasta,
        String precioOrganizacion,
        BigDecimal adicionalPorInscripcion
    ) {}
}



