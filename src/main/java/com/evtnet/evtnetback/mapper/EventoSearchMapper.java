package com.evtnet.evtnetback.mapper;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.utils.TimeUtil;

import java.util.List;

public final class EventoSearchMapper {
    private EventoSearchMapper(){}

    public static DTOResultadoBusquedaEventos toResultadoBusqueda(Evento e) {
        boolean esSuperevento = e.getSuperEvento() != null
                && e.getFechaHoraInicio() == null && e.getFechaHoraFin() == null;

        return new DTOResultadoBusquedaEventos(
                esSuperevento,
                e.getId(),
                e.getNombre(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                e.getPrecioInscripcion() == null ? null : e.getPrecioInscripcion().doubleValue(),
                e.getEspacio() == null ? null : e.getEspacio().getNombre(),
                e.getDisciplinasEvento() == null ? List.of()
                        : e.getDisciplinasEvento().stream().map(d -> d.getNombre()).toList(),
                null
        );
    }

    public static DTOResultadoBusquedaMisEventos toResultadoBusquedaMis(Evento e) {
        return new DTOResultadoBusquedaMisEventos(
                e.getId(),
                e.getNombre(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                e.getEspacio() == null ? "" : e.getEspacio().getNombre(),
                "participante",
                e.getInscripciones() == null ? null : e.getInscripciones().size()
        );
    }

    public static DTOEvento toDTOEvento(Evento e, boolean inscripto, boolean administrador) {
        DTOEvento.Espacio espacio = e.getEspacio() == null ? null :
                new DTOEvento.Espacio(e.getEspacio().getId(), e.getEspacio().getNombre());

        var modos =
            (e.getEventosModoEvento() != null && !e.getEventosModoEvento().isEmpty())
                ? e.getEventosModoEvento().stream().map(eme -> eme.getModoEvento().getNombre()).toList()
                : (e.getModoEvento() != null ? List.of(e.getModoEvento().getNombre()) : List.<String>of());

        var disciplinas = e.getDisciplinasEvento() == null ? List.<String>of()
                : e.getDisciplinasEvento().stream().map(d -> d.getNombre()).toList();

        var inscriptos = e.getInscripciones() == null ? List.<DTOEvento.Inscripto>of()
                : e.getInscripciones().stream()
                    .filter(i -> i.getUsuario() != null)
                    .map(i -> new DTOEvento.Inscripto(
                            i.getUsuario().getUsername(),
                            i.getUsuario().getNombre(),
                            i.getUsuario().getApellido()))
                    .toList();

        DTOEvento.Ubicacion ubic = new DTOEvento.Ubicacion(
                e.getLatitudUbicacion() == null ? null : e.getLatitudUbicacion().doubleValue(),
                e.getLongitudUbicacion() == null ? null : e.getLongitudUbicacion().doubleValue()
        );

        DTOEvento.Superevento sup = (e.getSuperEvento() == null) ? null
                : new DTOEvento.Superevento(e.getSuperEvento().getId(), e.getSuperEvento().getNombre());

        return new DTOEvento(
                e.getNombre(),
                e.getDescripcion(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                e.getPrecioInscripcion() == null ? 0d : e.getPrecioInscripcion().doubleValue(),
                modos,
                disciplinas,
                espacio,
                e.getDireccionUbicacion(),
                ubic,
                sup,
                inscripto,
                inscriptos,
                administrador,
                null
        );
    }
}
