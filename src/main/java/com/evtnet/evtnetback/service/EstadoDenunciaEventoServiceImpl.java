package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.EstadoDenunciaEvento;
import com.evtnet.evtnetback.repository.EstadoDenunciaEventoRepository;
import com.evtnet.evtnetback.dto.estadoDenunciaEvento.DTOEstadoDenunciaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class EstadoDenunciaEventoServiceImpl extends BaseServiceImpl <EstadoDenunciaEvento, Long> implements EstadoDenunciaEventoService {

    private final EstadoDenunciaEventoRepository estadoDenunciaEventoRepository;
    
    public EstadoDenunciaEventoServiceImpl(EstadoDenunciaEventoRepository estadoDenunciaEventoRepository) {
        super(estadoDenunciaEventoRepository);
        this.estadoDenunciaEventoRepository=estadoDenunciaEventoRepository;
    }

    @Override
    public Page<DTOEstadoDenunciaEvento> obtenerListaEstadoDenunciaEvento(Pageable pageable) throws Exception {
        Specification<EstadoDenunciaEvento> spec = Specification.where(null);
        Page<EstadoDenunciaEvento> estadosDenunciaEvento = estadoDenunciaEventoRepository.findAll(spec, pageable);
        return estadosDenunciaEvento
                .map(me->DTOEstadoDenunciaEvento.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .descripcion(me.getDescripcion())
                        .fechaAlta(me.getFechaHoraAlta() == null ? null
                                :me.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .fechaBaja(me.getFechaHoraBaja() == null ? null
                                :me.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build()
                );
    }

    @Override
    public DTOEstadoDenunciaEvento obtenerEstadoDenunciaEventoCompleto(Long id) throws Exception {
        EstadoDenunciaEvento estadoDenunciaEvento = estadoDenunciaEventoRepository.findById(id).get();
        return DTOEstadoDenunciaEvento.builder()
                .id(estadoDenunciaEvento.getId())
                .nombre(estadoDenunciaEvento.getNombre())
                .descripcion(estadoDenunciaEvento.getDescripcion())
                .build();
    }

    @Override
    public void altaEstadoDenunciaEvento(DTOEstadoDenunciaEvento estadoDenunciaEvento) throws Exception {
        this.save(EstadoDenunciaEvento.builder()
                .nombre(estadoDenunciaEvento.getNombre())
                .descripcion(estadoDenunciaEvento.getDescripcion())
                .fechaHoraAlta(LocalDateTime.now())
                .build());
    }

    @Override
    public void modificarEstadoDenunciaEvento(DTOEstadoDenunciaEvento EstadoDenunciaEvento) throws Exception {
        estadoDenunciaEventoRepository.update(EstadoDenunciaEvento.getId(), EstadoDenunciaEvento.getNombre(), EstadoDenunciaEvento.getDescripcion());
    }

    @Override
    public void bajaEstadoDenunciaEvento(Long id) throws Exception {
        estadoDenunciaEventoRepository.delete(id, LocalDateTime.now());
    }
}
