package com.evtnet.evtnetback.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.Entities.Espacio;

import com.evtnet.evtnetback.Repositories.BaseRepository;

@Repository
public interface EspacioRepository extends BaseRepository<Espacio, Long> {

    // 🔹 Busca todos los espacios cuyo propietario sea el usuario indicado
    @Query("""
        SELECT e FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE u.username = :username
          AND tae.nombre = 'Propietario'
    """)
    List<Espacio> findByPropietarioUsername(@Param("username") String username);

    // 🔹 Verifica si un espacio pertenece al usuario
    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE e.id = :id
          AND u.username = :username
          AND tae.nombre = 'Propietario'
    """)
    boolean existsByIdAndPropietario_Username(@Param("id") Long id, @Param("username") String username);
    
    @Query("""
        SELECT e FROM Espacio e
        JOIN e.administradoresEspacio ae
        JOIN ae.usuario u
        JOIN ae.tipoAdministradorEspacio tae
        WHERE u.username = :username
          AND tae.nombre = 'Propietario'
    """)
    List<Espacio> findAllByPropietario_Username(String username);
}

