package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.SEPEstado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SEPEstadoRepository extends BaseRepository <SEPEstado, Long> {
    @Query("""
    SELECT sepEstado
    FROM SEPEstado sepEstado
    WHERE sepEstado.solicitudEspacioPublico.id = :idSEP
      AND sepEstado.fechaHoraBaja is null
""")
    SEPEstado findUltimoBySEP(@Param("idSEP") Long idSEP);

    @Query("""
    SELECT sepEstado
    FROM SEPEstado sepEstado
    JOIN sepEstado.responsable
    WHERE sepEstado.solicitudEspacioPublico.id = :idSEP
    ORDER BY sepEstado.id
""")
    List<SEPEstado> findAllBySEP(@Param("idSEP") Long idSEP);
}
