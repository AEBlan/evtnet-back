package com.evtnet.evtnetback.repository;

//import java.util.Optional;

import com.evtnet.evtnetback.entity.AdministradorEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdministradorEspacioRepository extends BaseRepository<AdministradorEspacio,Long> {
    @Query("""
        SELECT ae
        FROM AdministradorEspacio ae
        JOIN ae.usuario u
        WHERE ae.espacio.id = :id
          AND u.username = :username
          AND ae.fechaHoraBaja is null
    """)
    AdministradorEspacio findByEspacioAndUser(@Param("id") Long id, @Param("username") String username);
}

