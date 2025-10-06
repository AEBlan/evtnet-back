package com.evtnet.evtnetback.Repositories.specs;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaEventos;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaMisEventos;
import com.evtnet.evtnetback.utils.TimeUtil;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.List;

public final class EventoSpecs {
    private EventoSpecs(){}

    // 🔹 Buscar TODOS los eventos (exploración general)
    public static Specification<Evento> byFiltroBusqueda(DTOBusquedaEventos f) {
        return Specification.allOf(
                textoLike(f.texto()),
                rangoFechaInterseca(f.fechaDesde(), f.fechaHasta()),
                precioMax(f.precioLimite()),
                conDisciplinasEvento(f.disciplinas()),
                conModos(f.modos()),
                espaciosNoRegistrados(f.espaciosNoRegistrados())
        );
    }

    // 🔹 Buscar SOLO mis eventos (organizador / administrador / participante)
    public static Specification<Evento> byFiltroMisEventos(DTOBusquedaMisEventos f, String username) {
        return Specification.allOf(
                textoLike(f.texto()),
                rangoFechaInterseca(f.fechaDesde(), f.fechaHasta()),
                Specification.anyOf(
                        f.organizador() ? esOrganizador(username) : null,
                        f.administrador() ? esAdministrador(username) : null,
                        f.participante() ? esParticipante(username) : null
                )
        );
    }

    // ---- helpers comunes ----
    static Specification<Evento> textoLike(String t) {
        if (t == null || t.isBlank()) return null;
        String like = "%" + t.trim().toLowerCase() + "%";
        return (root, cq, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombre")), like),
                cb.like(cb.lower(root.get("descripcion")), like)
        );
    }

    static Specification<Evento> rangoFechaInterseca(Long desdeMs, Long hastaMs) {
        if (desdeMs == null || hastaMs == null) return null;
        LocalDateTime d = TimeUtil.fromMillis(desdeMs);
        LocalDateTime h = TimeUtil.fromMillis(hastaMs);
        return (root, cq, cb) -> cb.and(
                cb.lessThanOrEqualTo(root.get("fechaHoraInicio"), h),
                cb.greaterThanOrEqualTo(root.get("fechaHoraFin"), d)
        );
    }

    static Specification<Evento> precioMax(Double max) {
        if (max == null) return null;
        return (root, cq, cb) -> cb.lessThanOrEqualTo(root.get("precioInscripcion"), max);
    }

    static Specification<Evento> conDisciplinasEvento(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, cq, cb) -> {
            var join = root.join("disciplinasEvento");
            cq.distinct(true);
            return join.get("id").in(ids);
        };
    }

    static Specification<Evento> conModos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return (root, cq, cb) -> root.get("modoEvento").get("id").in(ids);
    }

    static Specification<Evento> espaciosNoRegistrados(boolean flag) {
        if (!flag) return null;
        return (root, cq, cb) -> cb.isNull(root.get("espacio"));
    }

    // ---- helpers para MIS EVENTOS ----
    // ---- helpers para MIS EVENTOS ----
    static Specification<Evento> esOrganizador(String username) {
        return (root, cq, cb) ->
                cb.equal(root.join("organizador", JoinType.LEFT).get("username"), username);
    }

    static Specification<Evento> esAdministrador(String username) {
        return (root, cq, cb) ->
                cb.equal(root.join("administradoresEvento", JoinType.LEFT)
                            .join("usuario", JoinType.LEFT).get("username"), username);
    }

    static Specification<Evento> esParticipante(String username) {
        return (root, cq, cb) ->
                cb.equal(root.join("inscripciones", JoinType.LEFT)
                            .join("usuario", JoinType.LEFT).get("username"), username);
    }

}
