package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ExcepcionHorarioEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ExcepcionHorarioEspacioRepository extends BaseRepository <ExcepcionHorarioEspacio, Long> {
    @Query("""
    SELECT ehe
    FROM ExcepcionHorarioEspacio ehe
    """)
    ExcepcionHorarioEspacio findByCronograma(@Param("idCronograma") Long idCronograma);

    @Query("""
    SELECT CASE WHEN COUNT(ehe) > 0 THEN TRUE ELSE FALSE END
    FROM ExcepcionHorarioEspacio ehe
    WHERE ehe.configuracionHorarioEspacio.id= :idCronograma
        AND ehe.fechaHoraDesde < :fechaHoraHasta
        AND ehe.fechaHoraHasta > :fechaHoraDesde
    """)
    boolean existeSuperpuesto(@Param("fechaHoraDesde") LocalDateTime fechaHoraDesde, @Param("fechaHoraHasta")LocalDateTime fechaHoraHasta, @Param("idCronograma") Long idCronograma);

    @Query("""
    SELECT e
    FROM ExcepcionHorarioEspacio e
    JOIN e.configuracionHorarioEspacio che
    WHERE che.subEspacio.id = :idSubEspacio
      AND CAST(e.fechaHoraDesde AS DATE) <= :fechaFin
      AND CAST(e.fechaHoraHasta AS DATE) >= :fechaInicio
""")
    List<ExcepcionHorarioEspacio> findExcepcionesPorSubEspacio(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

}
