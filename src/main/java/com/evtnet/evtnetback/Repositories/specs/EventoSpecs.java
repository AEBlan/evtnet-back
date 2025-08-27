package com.evtnet.evtnetback.Repositories.specs;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaEventos;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaMisEventos;
import com.evtnet.evtnetback.utils.TimeUtil;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public final class EventoSpecs {
    private EventoSpecs(){}

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

    public static Specification<Evento> byFiltroMisEventos(DTOBusquedaMisEventos f) {
        return Specification.allOf(
                textoLike(f.texto()),
                rangoFechaInterseca(f.fechaDesde(), f.fechaHasta())
        );
    }

    // ---- helpers ----
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
            var join = root.join("disciplinasEvento"); // List<DisciplinaEvento>
            cq.distinct(true);
            return join.get("id").in(ids);
        };
    }

    static Specification<Evento> conModos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return null;
        // Usamos ManyToOne (rápido). Si preferís intermedia, cambia a eventosModoEvento.join("modoEvento")
        return (root, cq, cb) -> root.get("modoEvento").get("id").in(ids);
    }

    static Specification<Evento> espaciosNoRegistrados(boolean flag) {
        if (!flag) return null;
        return (root, cq, cb) -> cb.isNull(root.get("espacio"));
    }
}

