package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.dto.espacios.DTOEvento;
import com.evtnet.evtnetback.entity.Espacio;
import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudEspacioPublicoRepository extends BaseRepository <SolicitudEspacioPublico, Long> {
    @Query("""
    SELECT DISTINCT sep FROM SolicitudEspacioPublico sep
    WHERE sep.latitudUbicacion BETWEEN :latitudDesde AND :latitudHasta
      AND sep.longitudUbicacion BETWEEN :longitudDesde AND :longitudHasta
""")
    List<SolicitudEspacioPublico> findSolicitudesByUbicacion(@Param("latitudDesde") BigDecimal latitudDesde, @Param("latitudHasta") BigDecimal latitudHasta, @Param("longitudDesde")BigDecimal longitudDesde, @Param("longitudHasta") BigDecimal longitudHasta);

    @Query("""
    SELECT DISTINCT sep FROM SolicitudEspacioPublico sep
    WHERE (
        LOWER(sep.nombreEspacio) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sep.descripcion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sep.direccionUbicacion) LIKE CONCAT('%', LOWER(:texto), '%')
        OR LOWER(sep.justificacion) LIKE CONCAT('%', LOWER(:texto), '%')
      )
""")
    List<SolicitudEspacioPublico> findSolicitudesByTexto(@Param("texto") String texto);

    @Query("""
    SELECT DISTINCT sep FROM SolicitudEspacioPublico sep
    WHERE sep.espacio.id IN (:espacios)
""")
    List<SolicitudEspacioPublico> findSolicitudesByEspacio(@Param("espacios")List<Long>espacios);

    @Query("""
    SELECT DISTINCT sep FROM SolicitudEspacioPublico sep
    JOIN sep.sepEstados sepe
    WHERE sepe.estadoSEP.id IN (:estados)
""")
    List<SolicitudEspacioPublico> findSolicitudesByEstado(@Param("estados")List<Long>estados);

    @Query("""
    SELECT DISTINCT sep
    FROM SolicitudEspacioPublico sep
    WHERE FUNCTION('DATE',sep.fechaHoraAlta) BETWEEN :fechaIngresoDesde AND :fechaIngresoHasta
""")
    List<SolicitudEspacioPublico> findSolicitudesByFechaIngreso(
            @Param("fechaIngresoDesde") LocalDate fechaIngresoDesde,
            @Param("fechaIngresoHasta") LocalDate fechaIngresoHasta);

    @Query("""
    SELECT DISTINCT sep
    FROM SolicitudEspacioPublico sep
    JOIN sep.sepEstados sepEstado
    WHERE FUNCTION('DATE',sepEstado.fechaHoraAlta) BETWEEN :fechaCambioDesde AND :fechaCambioHasta
        AND sepEstado.fechaHoraBaja is null
""")
    List<SolicitudEspacioPublico> findSolicitudesByFechaCambioEstado(
            @Param("fechaCambioDesde") LocalDate fechaCambioDesde,
            @Param("fechaCambioHasta") LocalDate fechaCambioHasta);
}
