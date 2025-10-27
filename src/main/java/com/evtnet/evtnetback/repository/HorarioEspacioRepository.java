package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.HorarioEspacio;
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
        AND :fechaEvento BETWEEN CAST(che.fechaDesde AS DATE) AND CAST(che.fechaHasta AS DATE)
    """)
    List<HorarioEspacio> findBySubEspacioYFecha(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaEvento")LocalDate fechaEvento, @Param("diaSemana")String diaSemana);

    @Query("""
        SELECT he
        FROM HorarioEspacio he
        JOIN he.configuracionHorarioEspacio che
        WHERE che.subEspacio.id = :idSubEspacio
          AND CAST(che.fechaDesde AS DATE) <= :fechaFin
          AND CAST(che.fechaHasta AS DATE) >= :fechaInicio
        ORDER BY he.diaSemana, he.horaDesde
    """)
    List<HorarioEspacio> findHorariosOcupados(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);
}
