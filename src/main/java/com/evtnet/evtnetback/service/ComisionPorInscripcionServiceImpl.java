package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComisionPorInscripcion;
import com.evtnet.evtnetback.repository.ComisionPorInscripcionRepository;
import com.evtnet.evtnetback.dto.comisionPorInscripcion.DTOComisionPorInscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ComisionPorInscripcionServiceImpl extends BaseServiceImpl <ComisionPorInscripcion, Long> implements ComisionPorInscripcionService {

    private final ComisionPorInscripcionRepository comisionPorInscripcionRepository;
    
    public ComisionPorInscripcionServiceImpl(ComisionPorInscripcionRepository comisionPorInscripcionRepository) {
        super(comisionPorInscripcionRepository);
        this.comisionPorInscripcionRepository = comisionPorInscripcionRepository;
    }

    @Override
    public Page<DTOComisionPorInscripcion> obtenerListaComisionPorInscripcion(Pageable pageable) throws Exception {
        Specification<ComisionPorInscripcion> spec = Specification.where(null);
        Page<ComisionPorInscripcion> comisionesPorInscripcion = comisionPorInscripcionRepository.findAll(spec, pageable);
        return comisionesPorInscripcion
                .map(cpi->DTOComisionPorInscripcion.builder()
                        .id(cpi.getId())
                        .montoLimite(cpi.getMontoLimite())
                        .porcentaje(cpi.getPorcentaje())
                        .fechaDesde(cpi.getFechaDesde() == null ? null
                                :cpi.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .fechaHasta(cpi.getFechaHasta() == null ? null
                                :cpi.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build()
                );
    }

    @Override
    public DTOComisionPorInscripcion obtenerComisionPorInscripcionCompleto(Long id) throws Exception {
        ComisionPorInscripcion comisionPorInscripcion = comisionPorInscripcionRepository.findById(id).get();
        return DTOComisionPorInscripcion.builder()
                .id(comisionPorInscripcion.getId())
                .montoLimite(comisionPorInscripcion.getMontoLimite())
                .porcentaje(comisionPorInscripcion.getPorcentaje())
                .fechaDesde(comisionPorInscripcion.getFechaDesde() == null ? null
                    :comisionPorInscripcion.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(comisionPorInscripcion.getFechaHasta() == null ? null
                        :comisionPorInscripcion.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    @Override
    public void altaComisionPorInscripcion(DTOComisionPorInscripcion comisionPorInscripcion) throws Exception {
        this.save(ComisionPorInscripcion.builder()
                .montoLimite(comisionPorInscripcion.getMontoLimite())
                .porcentaje(comisionPorInscripcion.getPorcentaje())
                .fechaDesde(Instant.ofEpochMilli(comisionPorInscripcion.getFechaDesde())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fechaHasta(Instant.ofEpochMilli(comisionPorInscripcion.getFechaHasta())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build());
    }

    @Override
    public void modificarComisionPorInscripcion(DTOComisionPorInscripcion comisionPorInscripcion) throws Exception {
        LocalDateTime fechaDesde=Instant.ofEpochMilli(comisionPorInscripcion.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime fechaHasta=Instant.ofEpochMilli(comisionPorInscripcion.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        comisionPorInscripcionRepository.update(comisionPorInscripcion.getId(), comisionPorInscripcion.getMontoLimite(), comisionPorInscripcion.getPorcentaje(), fechaDesde, fechaHasta);
    }

    @Override
    public void bajaComisionPorInscripcion(Long id) throws Exception {
        comisionPorInscripcionRepository.delete(id, LocalDateTime.now());
    }
    
}
