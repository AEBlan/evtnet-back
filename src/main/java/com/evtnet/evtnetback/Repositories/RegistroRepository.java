package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Registro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends BaseRepository<Registro, Long> {
    
    Optional<Registro> findByNombre(String nombre);

    @Query("select distinct t.nombre from Registro r JOIN r.tipos t WHERE r.nombre = :registro")
    List<String> obtenerTipos(@Param("registro") String registro);

    @Query("select distinct s.nombre from Registro r JOIN r.subtipos s WHERE r.nombre = :registro")
    List<String> obtenerSubtipos(@Param("registro") String registro);

}
