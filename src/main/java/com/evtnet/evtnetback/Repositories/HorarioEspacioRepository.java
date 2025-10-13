package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.HorarioEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface HorarioEspacioRepository extends BaseRepository <HorarioEspacio, Long> {
    @Query("""
    SELECT CASE WHEN COUNT(he) > 0 THEN TRUE ELSE FALSE END
    FROM HorarioEspacio he
    WHERE he.configuracionHorarioEspacio.id= :idCronograma
        AND he.diaSemana = :diaSemana
        AND he.horaDesde < :horaHasta
        AND he.horaHasta > :horaDesde
    """)
    boolean existeSuperpuesto(@Param("horaDesde")LocalTime horaDesde, @Param("horaHasta")LocalTime horaHasta, @Param("idCronograma") Long idCronograma, @Param("diaSemana") String diaSemana);

    @Query("""
    SELECT he
    FROM HorarioEspacio he
    JOIN he.configuracionHorarioEspacio che
    WHERE che.subEspacio.id=:idSubEspacio
        AND he.diaSemana=:diaSemana
        AND :fechaEvento BETWEEN che.fechaDesde AND che.fechaHasta
    """)
    List<HorarioEspacio> findBySubEspacioYFecha(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaEvento")LocalDate fechaEvento, @Param("diaSemana")String diaSemana);

    @Query("""
        SELECT he
        FROM HorarioEspacio he
        JOIN he.configuracionHorarioEspacio che
        WHERE che.subEspacio.id = :idSubEspacio
          AND che.fechaDesde <= :fechaFin
          AND che.fechaHasta >= :fechaInicio
        ORDER BY he.diaSemana, he.horaDesde
    """)
    List<HorarioEspacio> findHorariosOcupados(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}
