package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.ImagenEspacio;
import java.util.Optional;
import java.util.List;

public interface ImagenEspacioRepository extends BaseRepository<ImagenEspacio, Long> {
    Optional<ImagenEspacio> findTopByEspacio_IdOrderByOrdenDesc(Long espacioId);
    List<ImagenEspacio> findByEspacio_IdOrderByOrdenAsc(Long espacioId);
}

