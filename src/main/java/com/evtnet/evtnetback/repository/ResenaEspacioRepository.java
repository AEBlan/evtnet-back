package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ResenaEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaEspacioRepository extends BaseRepository <ResenaEspacio, Long> {
    @Query("""
        SELECT re
        FROM ResenaEspacio re
        JOIN re.usuario
        WHERE re.espacio.id=:idEspacio
    """)
    List<ResenaEspacio> resenasByEspacio(@Param("idEspacio") Long idEspacio);
}
