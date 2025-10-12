package com.evtnet.evtnetback.Repositories;

//import java.util.Optional;

import com.evtnet.evtnetback.Entities.AdministradorEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdministradorEspacioRepository extends BaseRepository<AdministradorEspacio,Long> {
    @Query("""
        SELECT ae
        FROM AdministradorEspacio ae
        JOIN ae.usuario u
        WHERE ae.espacio.id = :id
          AND u.username = :username
    """)
    AdministradorEspacio findByEspacioAndUser(@Param("id") Long id, @Param("username") String username);
}

