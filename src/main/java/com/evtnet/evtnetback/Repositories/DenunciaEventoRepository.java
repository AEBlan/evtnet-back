package com.evtnet.evtnetback.Repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.*;

import com.evtnet.evtnetback.Entities.DenunciaEvento;
import com.evtnet.evtnetback.dto.usuarios.DTODenunciaUsuario;

@Repository
public interface DenunciaEventoRepository extends BaseRepository <DenunciaEvento, Long> {
   @Query("""
    SELECT new com.evtnet.evtnetback.dto.usuarios.DTODenunciaUsuario(
        COALESCE(ult.fechaHoraDesde, ev.fechaHoraInicio),
        de.descripcion,
        org.nombre, org.apellido, org.username, org.mail,
        den.nombre, den.apellido, den.username, den.mail
    )
    FROM DenunciaEvento de
        JOIN de.evento ev
        JOIN ev.organizador org
        JOIN de.denunciante den
        LEFT JOIN DenunciaEventoEstado ult
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