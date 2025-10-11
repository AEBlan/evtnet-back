package com.evtnet.evtnetback.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.*;

import com.evtnet.evtnetback.Entities.DenunciaEvento;
import com.evtnet.evtnetback.dto.usuarios.DTODenunciaUsuario;

import com.evtnet.evtnetback.Repositories.BaseRepository;

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
}