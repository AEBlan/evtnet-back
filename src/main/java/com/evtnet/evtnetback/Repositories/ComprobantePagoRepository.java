package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.ComprobantePago;

import com.evtnet.evtnetback.Repositories.BaseRepository;

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


