package com.evtnet.evtnetback.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import com.evtnet.evtnetback.entity.DenunciaEvento;
import com.evtnet.evtnetback.dto.usuarios.DTODenunciaUsuario;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface DenunciaEventoRepository extends BaseRepository <DenunciaEvento, Long> {
   @Query("""
        SELECT DISTINCT de
        FROM DenunciaEvento de
            JOIN FETCH de.evento ev
            LEFT JOIN FETCH ev.administradoresEvento ae
            LEFT JOIN FETCH ae.usuario org
            LEFT JOIN FETCH de.denunciante den
            LEFT JOIN FETCH DenunciaEventoEstado ult
                ON ult.denunciaEvento = de
                AND ult.fechaHoraDesde = (
                        SELECT MAX(x.fechaHoraDesde)
                        FROM DenunciaEventoEstado x
                        WHERE x.denunciaEvento = de
                )
        WHERE den.username = :username
        ORDER BY COALESCE(ult.fechaHoraDesde, ev.fechaHoraInicio) DESC
        """)
    Page<DTODenunciaUsuario> pageDenunciasUsuario(@Param("username") String username, Pageable pageable);

    @Query("SELECT DISTINCT d FROM DenunciaEvento d " +
            "LEFT JOIN d.estados e ON e.fechaHoraHasta IS NULL " +
            "LEFT JOIN d.estados e2 " +
            "WHERE (" +
            "   :texto IS NULL " +
            "   OR LOWER(d.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) " +
            "   OR LOWER(d.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.denunciante.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.denunciante.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.denunciante.username) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.denunciante.mail) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.denunciante.dni) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.evento.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            "   OR LOWER(d.evento.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))" +
            ") " +
            "AND (:estados IS NULL OR e2.estadoDenunciaEvento.id IN :estados AND e2.fechaHoraHasta IS NULL) " +
            "AND (:fechaIngresoDesde IS NULL OR d.fechaHoraAlta >= :fechaIngresoDesde) " +
            "AND (:fechaIngresoHasta IS NULL OR d.fechaHoraAlta <= :fechaIngresoHasta) " +
            "AND (:fechaCambioEstadoDesde IS NULL OR e.fechaHoraDesde >= :fechaCambioEstadoDesde) " +
            "AND (:fechaCambioEstadoHasta IS NULL OR e.fechaHoraDesde <= :fechaCambioEstadoHasta) " +
            "ORDER BY " +
            "CASE WHEN :orden = 'FECHA_CAMBIO_ESTADO_ASC' THEN e.fechaHoraDesde END ASC, " +
            "CASE WHEN :orden = 'FECHA_CAMBIO_ESTADO_DESC' THEN e.fechaHoraDesde END DESC, " +
            "CASE WHEN :orden = 'FECHA_DENUNCIA_ASC' THEN d.fechaHoraAlta END ASC, " +
            "CASE WHEN :orden = 'FECHA_DENUNCIA_DESC' THEN d.fechaHoraAlta END DESC, " +
            "d.id ASC")
    Page<DenunciaEvento> buscarDenuncias(
            @Param("texto") String texto,
            @Param("estados") List<Long> estados,
            @Param("fechaIngresoDesde") LocalDateTime fechaIngresoDesde,
            @Param("fechaIngresoHasta") LocalDateTime fechaIngresoHasta,
            @Param("fechaCambioEstadoDesde") LocalDateTime fechaCambioEstadoDesde,
            @Param("fechaCambioEstadoHasta") LocalDateTime fechaCambioEstadoHasta,
            @Param("orden") String orden,
            Pageable pageable
    );

}