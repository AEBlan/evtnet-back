package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Registro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroRepository extends BaseRepository<Registro, Long> {
    
    Optional<Registro> findByNombre(String nombre);

    @Query("select distinct e.nombre from EntidadRegistro e")
    List<String> obtenerEntidades();

    @Query("select distinct a.nombre from AccionRegistro a")
    List<String> obtenerAcciones();

}
