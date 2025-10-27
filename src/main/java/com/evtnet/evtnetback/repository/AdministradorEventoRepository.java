package com.evtnet.evtnetback.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.entity.AdministradorEvento;
import java.util.Optional;

@Repository
public interface AdministradorEventoRepository extends BaseRepository <AdministradorEvento, Long>{

    Optional<AdministradorEvento> findByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(
        Long eventoId, String username
    );
    
    @Query("""
    select case when count(ae) > 0 then true else false end
    from AdministradorEvento ae
    where ae.evento.id = :idEvento
      and ae.usuario.id = :idUsuario
      and ae.fechaHoraBaja is null
    """)
    boolean existeAdministradorActivo(@Param("idEvento") Long idEvento,
                                    @Param("idUsuario") Long idUsuario);


}
