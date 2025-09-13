package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.CalificacionTipo;
import com.evtnet.evtnetback.Repositories.CalificacionTipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionTipoServiceImpl extends BaseServiceImpl<CalificacionTipo, Long>
        implements CalificacionTipoService {

    private final CalificacionTipoRepository repository;

    public CalificacionTipoServiceImpl(CalificacionTipoRepository repository) {
        super(repository);               // BaseServiceImpl necesita el BaseRepository
        this.repository = repository;
    }

    @Override
    public List<CalificacionTipo> findActivosOrdenados() {
        return repository.findByFechaHoraBajaIsNullOrderByNombreAsc();
    }
}
