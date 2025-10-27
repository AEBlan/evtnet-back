package com.evtnet.evtnetback.repository.specs;

import com.evtnet.evtnetback.entity.DenunciaEvento;
import com.evtnet.evtnetback.dto.eventos.DTOBusquedaDenunciasEventos;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public class DenunciaEventoSpecs {

    public static Specification<DenunciaEvento> byFiltro(DTOBusquedaDenunciasEventos filtro) {
        return (root, query, cb) -> {
            root.fetch("evento", JoinType.LEFT);
            root.fetch("denunciante", JoinType.LEFT);

            var predicates = cb.conjunction();

            // 🔎 Texto libre
            if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
                String like = "%" + filtro.getTexto().toLowerCase() + "%";
                predicates.getExpressions().add(cb.or(
                        cb.like(cb.lower(root.get("titulo")), like),
                        cb.like(cb.lower(root.get("descripcion")), like),
                        cb.like(cb.lower(root.get("denunciante").get("username")), like),
                        cb.like(cb.lower(root.get("evento").get("nombre")), like)
                ));
            }

            // 🔎 Estados
            if (filtro.getEstados() != null && !filtro.getEstados().isEmpty()) {
                predicates.getExpressions().add(
                        root.join("estados", JoinType.LEFT)
                            .get("estadoDenunciaEvento")
                            .get("id")
                            .in(filtro.getEstados())
                );
            }

            // 🔎 Fecha de ingreso → usamos el primer estado de la denuncia
            if (filtro.getFechaIngresoDesde() != null || filtro.getFechaIngresoHasta() != null) {
                var joinEstado = root.join("estados", JoinType.LEFT);

                if (filtro.getFechaIngresoDesde() != null) {
                    predicates.getExpressions().add(
                            cb.greaterThanOrEqualTo(joinEstado.get("fechaHoraDesde"), filtro.getFechaIngresoDesde())
                    );
                }
                if (filtro.getFechaIngresoHasta() != null) {
                    predicates.getExpressions().add(
                            cb.lessThanOrEqualTo(joinEstado.get("fechaHoraDesde"), filtro.getFechaIngresoHasta())
                    );
                }
            }

            // 🔎 Fecha de cambio de estado
            if (filtro.getFechaCambioEstadoDesde() != null || filtro.getFechaCambioEstadoHasta() != null) {
                var joinEstado = root.join("estados", JoinType.LEFT);

                if (filtro.getFechaCambioEstadoDesde() != null) {
                    predicates.getExpressions().add(
                            cb.greaterThanOrEqualTo(joinEstado.get("fechaHoraDesde"), filtro.getFechaCambioEstadoDesde())
                    );
                }
                if (filtro.getFechaCambioEstadoHasta() != null) {
                    predicates.getExpressions().add(
                            cb.lessThanOrEqualTo(joinEstado.get("fechaHoraDesde"), filtro.getFechaCambioEstadoHasta())
                    );
                }
            }

            return predicates;
        };
    }
}
