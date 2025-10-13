package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Usuario;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    Optional<Usuario> findByMail(String mail);
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByMail(String mail);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByMailIgnoreCase(String mail);
    boolean existsByDni(String dni);

    // Útil si querés evitar N+1 al traer grupos/tipos con un solo hit
    Optional<Usuario> findWithGruposByUsername(String username);

    Optional<Usuario> findWithRolesByUsername(String username);

    @Query("""
           select u
           from Usuario u
           where u.fechaHoraBaja is null
             and (
                 lower(u.username) like lower(concat('%', :q, '%'))
              or lower(u.nombre)   like lower(concat('%', :q, '%'))
              or lower(u.apellido) like lower(concat('%', :q, '%'))
             )
           """)
    List<Usuario> buscarPorTexto(@Param("q") String texto);

        @Query("""
        select u 
        from Usuario u
        where (lower(u.username) like lower(concat('%', :texto, '%'))
            or lower(u.nombre) like lower(concat('%', :texto, '%'))
            or lower(u.apellido) like lower(concat('%', :texto, '%')))
            and u.username <> :currentUser
            and u.id not in (
                select ug.usuario.id 
                from UsuarioGrupo ug 
                where ug.grupo.id = :idGrupo 
                and ug.fechaHoraBaja is null
            )
        """)
    List<Usuario> buscarUsuariosParaAgregar(@Param("idGrupo") Long idGrupo,
                                            @Param("texto") String texto,
                                            @Param("currentUser") String currentUser);
    Optional<Usuario> findByUsernameIgnoreCase(String username);

    @Query("select u from Usuario u where lower(u.username) in :usernames")
    List<Usuario> findAllByUsernameInLower(@Param("usernames") List<String> usernames);


    //Query para buscar no inscrptos en un evento
    @Query("""
    select u from Usuario u
    where u.fechaHoraBaja is null
      and (lower(u.username) like lower(concat('%', :texto, '%'))
        or lower(u.nombre) like lower(concat('%', :texto, '%'))
        or lower(u.apellido) like lower(concat('%', :texto, '%')))
      and u.id not in (
          select i.usuario.id from Inscripcion i where i.evento.id = :idEvento
      )
    """)
    List<Usuario> buscarNoInscriptos(@Param("idEvento") Long idEvento,
                                    @Param("texto") String texto);

    @Query("""
      select u
      from Usuario u
      join Inscripcion i on i.usuario.id = u.id
      where i.evento.id = :idEvento
        and u.fechaHoraBaja is null
        and (
            lower(u.username) like lower(concat('%', :texto, '%'))
            or lower(u.nombre)   like lower(concat('%', :texto, '%'))
            or lower(u.apellido) like lower(concat('%', :texto, '%'))
        )
        and u.id not in (
            select ae.usuario.id
            from AdministradorEvento ae
            where ae.evento.id = :idEvento
     )
    """)
    List<Usuario> buscarUsuariosNoAdministradores(@Param("idEvento") Long idEvento,
                                                  @Param("texto") String texto);

    @Query("""
    SELECT u
    FROM Usuario u
    WHERE u.fechaHoraBaja IS NULL
      AND (
          LOWER(u.username) LIKE LOWER(CONCAT('%', :texto, '%'))
          OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
          OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
      )
      AND u.id NOT IN (
          SELECT ae.usuario.id
          FROM AdministradorEspacio ae
          WHERE ae.espacio.id = :idEspacio
      )
    """)
    List<Usuario> buscarUsuariosNoAdministradoresEspacio(@Param("idEspacio") Long idEspacio, @Param("texto") String texto);

    @Query("""
      select u
      from Usuario u
      join u.administradoresEspacio ae
      where ae.espacio.id = :idEspacio
        and u.fechaHoraBaja is null
        and ae.fechaHoraBaja is null
    """)
    List<Usuario> buscarUsuariosAdministradoresEspacio(@Param("idEspacio") Long idEspacio);

}
