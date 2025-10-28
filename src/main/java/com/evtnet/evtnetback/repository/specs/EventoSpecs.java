package com.evtnet.evtnetback.repository.specs;

import com.evtnet.evtnetback.entity.Evento;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaEventos;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaMisEventos;
import com.evtnet.evtnetback.utils.TimeUtil;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

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
                conDisciplinasEvento(f.disciplinas())
                //conModos(f.modos()),
                //espaciosNoRegistrados(f.espaciosNoRegistrados())
        );
    }

    // 🔹 Buscar SOLO mis eventos (organizador / administrador / participante/encargado)
    public static Specification<Evento> byFiltroMisEventos(DTOBusquedaMisEventos f, String username) {
        // filtro de texto parcial (nombre o descripción)
        Specification<Evento> specTexto = textoLike(f.texto());
    
        // filtro por fechas (inicio y fin)
        Specification<Evento> specFechas = rangoFechaInterseca(f.fechaDesde(), f.fechaHasta());
    


        /*Specification<Evento> roles = Specification.anyOf(
                f.organizador() ? esOrganizador(username) : null,
                f.administrador() ? esAdministrador(username) : null,
                f.participante() ? esParticipante(username) : null,
                f.encargado() ? esEncargado(username) : null
        );
    
        // si no seleccionó ningún rol → traer todos
        if (!f.organizador() && !f.administrador() && !f.participante() && !f.encargado()) {
            roles = Specification.anyOf(
                    esOrganizador(username),
                    esAdministrador(username),
                    esParticipante(username),
                    esEncargado(username)
            );
        }*/
    
        return Specification.allOf(
                specTexto,
                specFechas
//                roles
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

    static Specification<Evento> esOrganizador(String username) {
        if (username == null || username.isBlank()) return null;
        return (root, cq, cb) -> {
            var joinAdmin = root.join("administradoresEvento", JoinType.LEFT);
            var joinTipo = joinAdmin.join("tipoAdministradorEvento");
            var joinUsuario = joinAdmin.join("usuario");
    
            cq.distinct(true);
    
            return cb.and(
                    cb.equal(joinUsuario.get("username"), username),
                    cb.equal(joinTipo.get("nombre"), "Organizador"),
                    cb.isNull(joinAdmin.get("fechaHoraBaja"))
            );
        };
    }
    
    static Specification<Evento> esAdministrador(String username) {
        if (username == null || username.isBlank()) return null;
        return (root, cq, cb) -> {
            var joinAdmin = root.join("administradoresEvento", JoinType.LEFT);
            var joinTipo = joinAdmin.join("tipoAdministradorEvento");
            var joinUsuario = joinAdmin.join("usuario");
    
            cq.distinct(true);
    
            return cb.and(
                    cb.equal(joinUsuario.get("username"), username),
                    cb.equal(joinTipo.get("nombre"), "Administrador"),
                    cb.isNull(joinAdmin.get("fechaHoraBaja"))
            );
        };
    }
    
    static Specification<Evento> esParticipante(String username) {
        if (username == null || username.isBlank()) return null;
        return (root, cq, cb) -> {
            var joinInscripcion = root.join("inscripciones", JoinType.LEFT);
            var joinUsuario = joinInscripcion.join("usuario");
    
            cq.distinct(true);
    
            return cb.and(
                    cb.equal(joinUsuario.get("username"), username),
                    cb.isNull(joinInscripcion.get("fechaHoraBaja"))
            );
        };
    }

    static Specification<Evento> esEncargado(String username) {
        if (username == null || username.isBlank()) return null;
        return (root, cq, cb) -> {
            // JOIN → evento → subEspacio → encargadosSubEspacio → usuario
            var joinSubespacio = root.join("subEspacio");
            var joinEncargado = joinSubespacio.join("encargadosSubEspacio", JoinType.LEFT);
            var joinUsuario = joinEncargado.join("usuario");
    
            cq.distinct(true);
    
            return cb.and(
                    cb.equal(joinUsuario.get("username"), username),
                    cb.isNull(joinEncargado.get("fechaHoraBaja"))
            );
        };
    }
    
    
}

