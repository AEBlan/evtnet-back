package com.evtnet.evtnetback.mapper;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EventoSearchMapper { 
    private EventoSearchMapper(){}

    public static DTOResultadoBusquedaEventos toResultadoBusqueda(Evento e) {
        boolean esSuperevento = e.getSuperEvento() != null
                && e.getFechaHoraInicio() == null && e.getFechaHoraFin() == null;

        List<String> disciplinas = (e.getDisciplinasEvento() == null)
                ? List.<String>of()
                : e.getDisciplinasEvento().stream()
                    .map(de -> de.getDisciplina() != null ? de.getDisciplina().getNombre() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        return new DTOResultadoBusquedaEventos(
                esSuperevento,
                e.getId(),
                e.getNombre(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                e.getPrecioInscripcion() == null ? null : e.getPrecioInscripcion().doubleValue(),
                e.getSubEspacio().getEspacio().getNombre(),
                disciplinas,
                null
        );
    }

    public static DTOResultadoBusquedaMisEventos toResultadoBusquedaMis(Evento e) {
        return new DTOResultadoBusquedaMisEventos(
                e.getId(),
                e.getNombre(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                e.getSubEspacio().getEspacio().getNombre(),
                "participante",
                e.getInscripciones() == null ? null : e.getInscripciones().size()
        );
    }

    public static DTOEvento toDTOEvento(Evento e, boolean inscripto, boolean administrador) {
        DTOEvento.Espacio espacio = new DTOEvento.Espacio(e.getSubEspacio().getEspacio().getId(), e.getSubEspacio().getEspacio().getNombre());

        // Disciplinas desde DisciplinaEvento -> Disciplina.nombre
        List<String> disciplinas = (e.getDisciplinasEvento() == null)
                ? List.<String>of()
                : e.getDisciplinasEvento().stream()
                    .map(de -> de.getDisciplina() != null ? de.getDisciplina().getNombre() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        // Inscriptos
        List<DTOEvento.Inscripto> inscriptos = (e.getInscripciones() == null)
                ? List.<DTOEvento.Inscripto>of()
                : e.getInscripciones().stream()
                    .filter(i -> i.getUsuario() != null)
                    .map(i -> new DTOEvento.Inscripto(
                            i.getUsuario().getUsername(),
                            i.getUsuario().getNombre(),
                            i.getUsuario().getApellido()))
                    .collect(Collectors.toList());

        DTOEvento.Ubicacion ubic = new DTOEvento.Ubicacion(
                e.getSubEspacio().getEspacio().getLatitudUbicacion().doubleValue(),
                e.getSubEspacio().getEspacio().getLongitudUbicacion().doubleValue()
        );

        DTOEvento.Superevento sup = (e.getSuperEvento() == null)
                ? null
                : new DTOEvento.Superevento(e.getSuperEvento().getId(), e.getSuperEvento().getNombre());

        return new DTOEvento(
                e.getNombre(),
                e.getDescripcion(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                e.getPrecioInscripcion() == null ? 0d : e.getPrecioInscripcion().doubleValue(),
                disciplinas,
                espacio,
                e.getSubEspacio().getEspacio().getDireccionUbicacion(),
                ubic,
                sup,
                inscripto,
                inscriptos,
                administrador,
                null
        );
    }
}
