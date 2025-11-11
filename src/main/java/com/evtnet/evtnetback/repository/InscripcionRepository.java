package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Inscripcion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface InscripcionRepository extends BaseRepository<Inscripcion, Long> {
    int countByEventoId(Long eventoId);
    int countByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(Long eventoId, String username);
    Optional<Inscripcion> findByEventoIdAndUsuarioUsername(Long eventoId, String username);

    @Query("""
        select coalesce(sum(1 + size(i.invitados)), 0)
        from Inscripcion i
        where i.evento.id = :eventoId
            and i.fechaHoraBaja is null
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
        select distinct i
        from Inscripcion i
        join i.usuario u
        left join i.invitados inv
        where i.evento.id = :idEvento
            and (
                lower(u.username) like lower(concat('%', :texto, '%'))
                or lower(u.nombre)   like lower(concat('%', :texto, '%'))
                or lower(u.apellido) like lower(concat('%', :texto, '%'))
                or lower(u.dni) like lower(concat('%', :texto, '%'))
                or lower(u.username) like lower(concat('%', :texto, '%'))
                or lower(inv.nombre) like lower(concat('%', :texto, '%'))
                or lower(inv.apellido) like lower(concat('%', :texto, '%'))
                or lower(inv.dni) like lower(concat('%', :texto, '%'))
            )
        order by i.fechaHoraBaja DESC NULLS FIRST, i.fechaHoraAlta ASC
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

