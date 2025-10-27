package com.evtnet.evtnetback.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.entity.SubEspacio;

@Repository
public interface SubEspacioRepository extends BaseRepository<SubEspacio, Long> {

    @Query("""
    SELECT se
    FROM SubEspacio se
    WHERE se.espacio.id = :idEspacio
""")
    List<SubEspacio> findAllByEspacio(@Param("idEspacio") Long idEspacio);

}
