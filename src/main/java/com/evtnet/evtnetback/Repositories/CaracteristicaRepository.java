package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Caracteristica;
import com.evtnet.evtnetback.dto.espacios.DTOCaracteristica;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaRepository extends BaseRepository <Caracteristica, Long> {
    @Query("""
    SELECT new com.evtnet.evtnetback.dto.espacios.DTOCaracteristica(ic.id, c.nombre)
    FROM Caracteristica c
    JOIN c.iconoCaracteristica ic
    JOIN c.subEspacio se
    WHERE se.id = :idSubEspacio
""")
    List<DTOCaracteristica> caracteristicas(@Param("idSubEspacio") Long idSubespacio);


    @Query("""
    SELECT c
    FROM Caracteristica c
    JOIN c.iconoCaracteristica ic
    WHERE c.subEspacio.id = :idSubEspacio
    """)
    List<Caracteristica> findBySubEspacio(@Param("idSubEspacio")Long idSubEspacio);

}
