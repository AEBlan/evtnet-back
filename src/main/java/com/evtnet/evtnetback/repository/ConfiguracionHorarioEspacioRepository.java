package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ConfiguracionHorarioEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConfiguracionHorarioEspacioRepository extends BaseRepository <ConfiguracionHorarioEspacio, Long> {
    @Query("""
        SELECT che
        FROM ConfiguracionHorarioEspacio che
        JOIN che.subEspacio se
        JOIN se.espacio e
        WHERE e.id = :idEspacio
    """)
    List<ConfiguracionHorarioEspacio> findAllByEspacio(@Param("idEspacio") Long idEspacio);

    @Query("""
    SELECT che
    FROM ConfiguracionHorarioEspacio che
    WHERE che.subEspacio.id = :idSubEspacio
        AND (:idCronograma is not null AND che.id != :idCronograma)
        AND (:fechaDesde BETWEEN che.fechaDesde AND che.fechaHasta OR :fechaHasta BETWEEN che.fechaDesde AND che.fechaHasta)
    """)
    List<ConfiguracionHorarioEspacio> findSuperpuestos(@Param("idSubEspacio") Long idSubEspacio, @Param("idCronograma") Long idCronograma, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);
}
