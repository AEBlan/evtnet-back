package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Usuario;

import com.evtnet.evtnetback.Repositories.BaseRepository;

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
           where u.fechaBaja is null
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

}
