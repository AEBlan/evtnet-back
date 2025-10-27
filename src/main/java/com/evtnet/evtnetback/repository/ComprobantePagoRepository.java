package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ComprobantePago;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ComprobantePagoRepository extends BaseRepository<ComprobantePago, Long> {

    @Query("""
        SELECT COALESCE(SUM(i.montoUnitario * i.cantidad), 0)
        FROM ItemComprobantePago i
        JOIN i.comprobantePago c
        LEFT JOIN c.evento e
        LEFT JOIN c.inscripcion ins
        WHERE (e.id = :idEvento OR ins.evento.id = :idEvento)
          AND i.pago.username = :username
    """)
    Optional<BigDecimal> totalPagadoPorEventoYUsuario(@Param("idEvento") long idEvento,
                                                      @Param("username") String username);
}


