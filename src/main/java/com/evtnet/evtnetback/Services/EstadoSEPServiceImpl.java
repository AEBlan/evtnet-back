package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.EstadoSEP;
import com.evtnet.evtnetback.Repositories.EstadoSEPRepository;
import com.evtnet.evtnetback.dto.estadoSEP.DTOEstadoSEP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EstadoSEPServiceImpl extends BaseServiceImpl <EstadoSEP, Long> implements EstadoSEPService {

    private final EstadoSEPRepository estadoSEPRepository;
    public EstadoSEPServiceImpl(EstadoSEPRepository estadoSEPRepository) {
        super(estadoSEPRepository);
        this.estadoSEPRepository = estadoSEPRepository;
    }

    @Override
    public Page<DTOEstadoSEP> obtenerListaEstadoSEP(Pageable pageable) throws Exception {
        Specification<EstadoSEP> spec = Specification.where(null);
        Page<EstadoSEP> estadosSEP = estadoSEPRepository.findAll(spec, pageable);
        return estadosSEP
                .map(me->DTOEstadoSEP.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .descripcion(me.getDescripcion())
                        .build()
                );
    }

    @Override
    public DTOEstadoSEP obtenerEstadoSEPCompleto(Long id) throws Exception {
        EstadoSEP estadoSEP = estadoSEPRepository.findById(id).get();
        return DTOEstadoSEP.builder()
                .id(estadoSEP.getId())
                .nombre(estadoSEP.getNombre())
                .descripcion(estadoSEP.getDescripcion())
                .build();
    }

    @Override
    public void altaEstadoSEP(DTOEstadoSEP estadoSEP) throws Exception {
        this.save(EstadoSEP.builder()
                .nombre(estadoSEP.getNombre())
                .descripcion(estadoSEP.getDescripcion())
                .build());
    }

    @Override
    public void modificarEstadoSEP(DTOEstadoSEP EstadoSEP) throws Exception {
        estadoSEPRepository.update(EstadoSEP.getId(), EstadoSEP.getNombre(), EstadoSEP.getDescripcion());
    }

    @Override
    public void bajaEstadoSEP(Long id) throws Exception {
        estadoSEPRepository.delete(id, LocalDateTime.now());
    }
    
}
