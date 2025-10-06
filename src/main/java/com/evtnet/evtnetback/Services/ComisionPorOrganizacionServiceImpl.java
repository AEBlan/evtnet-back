package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ComisionPorOrganizacion;
import com.evtnet.evtnetback.Entities.ComisionPorOrganizacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.ComisionPorOrganizacionRepository;
import com.evtnet.evtnetback.dto.comisionPorOrganizacion.DTOComisionPorOrganizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ComisionPorOrganizacionServiceImpl extends BaseServiceImpl <ComisionPorOrganizacion, Long> implements ComisionPorOrganizacionService {

    private final ComisionPorOrganizacionRepository comisionPorOrganizacionRepository;
    
    public ComisionPorOrganizacionServiceImpl(ComisionPorOrganizacionRepository comisionPorOrganizacionRepository) {
        super(comisionPorOrganizacionRepository);
        this.comisionPorOrganizacionRepository = comisionPorOrganizacionRepository;
    }

    @Override
    public Page<DTOComisionPorOrganizacion> obtenerListaComisionPorOrganizacion(Pageable pageable) throws Exception {
        Specification<ComisionPorOrganizacion> spec = Specification.where(null);
        Page<ComisionPorOrganizacion> comisionesPorOrganizacion = comisionPorOrganizacionRepository.findAll(spec, pageable);
        return comisionesPorOrganizacion
                .map(cpi->DTOComisionPorOrganizacion.builder()
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
    public DTOComisionPorOrganizacion obtenerComisionPorOrganizacionCompleto(Long id) throws Exception {
        ComisionPorOrganizacion comisionPorOrganizacion = comisionPorOrganizacionRepository.findById(id).get();
        return DTOComisionPorOrganizacion.builder()
                .id(comisionPorOrganizacion.getId())
                .montoLimite(comisionPorOrganizacion.getMontoLimite())
                .porcentaje(comisionPorOrganizacion.getPorcentaje())
                .fechaDesde(comisionPorOrganizacion.getFechaDesde() == null ? null
                        :comisionPorOrganizacion.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaHasta(comisionPorOrganizacion.getFechaHasta() == null ? null
                        :comisionPorOrganizacion.getFechaHasta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    @Override
    public void altaComisionPorOrganizacion(DTOComisionPorOrganizacion comisionPorOrganizacion) throws Exception {
        this.save(ComisionPorOrganizacion.builder()
                .montoLimite(comisionPorOrganizacion.getMontoLimite())
                .porcentaje(comisionPorOrganizacion.getPorcentaje())
                .fechaDesde(Instant.ofEpochMilli(comisionPorOrganizacion.getFechaDesde())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .fechaHasta(Instant.ofEpochMilli(comisionPorOrganizacion.getFechaHasta())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build());
    }

    @Override
    public void modificarComisionPorOrganizacion(DTOComisionPorOrganizacion comisionPorOrganizacion) throws Exception {
        LocalDateTime fechaDesde=Instant.ofEpochMilli(comisionPorOrganizacion.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime fechaHasta=Instant.ofEpochMilli(comisionPorOrganizacion.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        comisionPorOrganizacionRepository.update(comisionPorOrganizacion.getId(), comisionPorOrganizacion.getMontoLimite(), comisionPorOrganizacion.getPorcentaje(), fechaDesde, fechaHasta);
    }

    @Override
    public void bajaComisionPorOrganizacion(Long id) throws Exception {
        comisionPorOrganizacionRepository.delete(id, LocalDateTime.now());
    }
    
}
