package com.evtnet.evtnetback.mapper;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.utils.TimeUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EventoSearchMapper {

    private EventoSearchMapper() {}

    // 🔹 Mapea resultados para "Mis Eventos" (incluye todos los roles)
    public static DTOResultadoBusquedaMisEventos toResultadoBusquedaMis(Evento e, String username) {
        String rol = "participante"; // valor por defecto

        // 1️⃣ Verificar si es ORGANIZADOR o ADMINISTRADOR
        if (e.getAdministradoresEvento() != null) {
            var admin = e.getAdministradoresEvento().stream()
                    .filter(a -> a.getUsuario() != null && a.getUsuario().getUsername().equals(username))
                    .filter(a -> a.getFechaHoraBaja() == null)
                    .findFirst()
                    .orElse(null);

            if (admin != null && admin.getTipoAdministradorEvento() != null) {
                String tipo = admin.getTipoAdministradorEvento().getNombre();
                if ("Organizador".equalsIgnoreCase(tipo)) {
                    rol = "organizador";
                } else if ("Administrador".equalsIgnoreCase(tipo)) {
                    rol = "administrador";
                }
            }
        }

        // 2️⃣ Si no es admin ni organizador, revisar si es PARTICIPANTE
        if ("participante".equals(rol) && e.getInscripciones() != null) {
            boolean inscripto = e.getInscripciones().stream()
                    .anyMatch(i -> i.getUsuario() != null
                            && i.getUsuario().getUsername().equals(username)
                            && i.getFechaHoraBaja() == null);
            if (inscripto) rol = "participante";
        }

        // 3️⃣ Si no tiene rol anterior, revisar si es ENCARGADO
        /*if ("participante".equals(rol) && e.getSubEspacio() != null && e.getSubEspacio().getEncargadoSubEspacio() != null) {
                var encargado = e.getSubEspacio().getEncargadoSubEspacio();
                if (encargado.getUsuario() != null
                        && encargado.getUsuario().getUsername().equals(username)
                        && encargado.getFechaHoraBaja() == null) {
                rol = "encargado";
                }
        }*/
    

        return new DTOResultadoBusquedaMisEventos(
                e.getId(),
                e.getNombre(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                e.getSubEspacio() != null && e.getSubEspacio().getEspacio() != null
                        ? e.getSubEspacio().getEspacio().getNombre()
                        : null,
                rol,
                e.getInscripciones() == null ? null : e.getInscripciones().size()
        );
    }

    // 🔹 Conversión detallada de un Evento (por id)
    public static DTOEvento toDTOEvento(Evento e, boolean inscripto, boolean administrador) {
        DTOEvento.Espacio espacio = new DTOEvento.Espacio(
                e.getSubEspacio().getEspacio().getId(),
                e.getSubEspacio().getEspacio().getNombre()
        );

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
