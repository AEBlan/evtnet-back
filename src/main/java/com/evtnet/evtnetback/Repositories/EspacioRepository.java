package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.Entities.Espacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EspacioRepository extends BaseRepository<Espacio, Long> {

    // Duplicado por nombre + dirección (case-insensitive)
    @Query("""
        select (count(e) > 0) from Espacio e
        where lower(e.nombre) = lower(:nombre)
          and lower(e.direccionUbicacion) = lower(:direccion)
    """)
    boolean existeDuplicadoNombreDireccion(@Param("nombre") String nombre,
                                           @Param("direccion") String direccion);
}

