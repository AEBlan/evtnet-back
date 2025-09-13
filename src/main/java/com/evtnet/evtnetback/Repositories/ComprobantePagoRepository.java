package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.ComprobantePago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ComprobantePagoRepository extends BaseRepository<ComprobantePago, Long> {

    @Query("""
        select coalesce(sum(c.montoTotalBruto), 0)
        from ComprobantePago c
        where (c.evento.id = :idEvento or c.inscripcion.evento.id = :idEvento)
          and c.pago.username = :username
    """)
    Optional<BigDecimal> totalPagadoPorEventoYUsuario(@Param("idEvento") long idEvento,
                                                      @Param("username") String username);
}


