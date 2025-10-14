package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Inscripcion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface InscripcionRepository extends BaseRepository<Inscripcion, Long> {
    int countByEventoId(Long eventoId);
    int countByEventoIdAndUsuarioUsername(Long eventoId, String username);
    Optional<Inscripcion> findByEventoIdAndUsuarioUsername(Long eventoId, String username);

    @Query("""
        select coalesce(sum(1 + size(i.invitados)), 0)
        from Inscripcion i
        where i.evento.id = :eventoId
    """)
    int countParticipantesEfectivos(@Param("eventoId") Long eventoId);

    @Query("""
        select coalesce(max(size(i.invitados)), 0)
        from Inscripcion i
        where i.evento.id = :eventoId
            and i.fechaHoraBaja is null
    """)
    int maxInvitadosPorInscripcionVigente(@Param("eventoId") Long eventoId);

    @Query("""
        select i
        from Inscripcion i
        join i.usuario u
        where i.evento.id = :idEvento
        and (
            lower(u.username) like lower(concat('%', :texto, '%'))
        or lower(u.nombre)   like lower(concat('%', :texto, '%'))
        or lower(u.apellido) like lower(concat('%', :texto, '%'))
        )
    """)
    
    List<Inscripcion> findByEventoIdAndFiltro(@Param("idEvento") Long idEvento,
                                            @Param("texto") String texto);
    
    @Query("""
        select i
        from Inscripcion i
        join fetch i.usuario u
        where i.evento.id = :idEvento
            and i.fechaHoraBaja is null
    """)
    List<Inscripcion> findActivasByEventoId(@Param("idEvento") Long idEvento);
                                            
    @Query("""
        select count(i)
        from Inscripcion i
        where i.evento.id = :idEvento
            and i.usuario.username = :username
            and i.fechaHoraBaja is null
    """)
    int countActivasByEventoIdAndUsuarioUsername(@Param("idEvento") Long idEvento,
                                                    @Param("username") String username);
    
   
    @Query("""
        select i
        from Inscripcion i
        where i.evento.id = :idEvento
            and i.usuario.username = :username
            and i.fechaHoraBaja is null
    """)
    Optional<Inscripcion> findActivaByEventoIdAndUsuarioUsername(@Param("idEvento") Long idEvento,
                                                                    @Param("username") String username);
                                                    
}

