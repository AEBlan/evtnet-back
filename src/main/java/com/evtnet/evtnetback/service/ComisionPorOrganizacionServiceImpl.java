package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComisionPorInscripcion;
import com.evtnet.evtnetback.entity.ComisionPorOrganizacion;
import com.evtnet.evtnetback.repository.ComisionPorOrganizacionRepository;
import com.evtnet.evtnetback.dto.comisionPorOrganizacion.DTOComisionPorOrganizacion;
import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ComisionPorOrganizacionServiceImpl extends BaseServiceImpl <ComisionPorOrganizacion, Long> implements ComisionPorOrganizacionService {

    private final ComisionPorOrganizacionRepository comisionPorOrganizacionRepository;
    private final ParametroSistemaService parametroSistemaService;
    private final RegistroSingleton registroSingleton;
    
    public ComisionPorOrganizacionServiceImpl(ComisionPorOrganizacionRepository comisionPorOrganizacionRepository,
                                              ParametroSistemaService parametroSistemaService,
                                              RegistroSingleton registroSingleton) {
        super(comisionPorOrganizacionRepository);
        this.comisionPorOrganizacionRepository = comisionPorOrganizacionRepository;
        this.parametroSistemaService = parametroSistemaService;
        this.registroSingleton = registroSingleton;
    }

    @Override
    public Page<DTOComisionPorOrganizacion> obtenerListaComisionPorOrganizacion(int page, boolean activas, boolean noActivas) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHasta"),
                        Sort.Order.asc("fechaDesde")
                )
        );
        Specification<ComisionPorOrganizacion> spec = Specification.where(null);
        boolean activasFiltro = Boolean.TRUE.equals(activas);
        boolean noActivasFiltro = Boolean.TRUE.equals(noActivas);

        LocalDateTime hoy = LocalDateTime.now();

        if (activasFiltro && !noActivasFiltro) {
            spec = spec.and((root, cq, cb) -> cb.and(
                    cb.or(
                            cb.isNull(root.get("fechaHasta")),
                            cb.greaterThan(root.get("fechaHasta"), hoy)
                    ),
                    cb.lessThan(root.get("fechaDesde"), hoy)
            ));
        }

        if (!activasFiltro && noActivasFiltro) {
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.and(
                            cb.isNotNull(root.get("fechaHasta")),
                            cb.lessThan(root.get("fechaHasta"), hoy)
                    ),
                    cb.greaterThan(root.get("fechaDesde"), hoy)
            ));
        }
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
    public void altaComisionPorOrganizacion(DTOComisionPorOrganizacion dto) throws Exception {

        LocalDateTime fechaDesde = Instant.ofEpochMilli(dto.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime fechaHasta = Instant.ofEpochMilli(dto.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime hoy = LocalDateTime.now();

        if (comisionPorOrganizacionRepository.existsVigenteWithSameMonto(
                dto.getMontoLimite(), hoy, null)) {
            throw new Exception("Ya existe una comisión vigente con el mismo monto límite.");
        }

        ComisionPorOrganizacion comision=this.save(ComisionPorOrganizacion.builder()
                .montoLimite(dto.getMontoLimite())
                .porcentaje(dto.getPorcentaje())
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build());
        registroSingleton.write("Parametros", "comision_organizacion", "creacion", "ComisionOrganizacion de ID " + comision.getId() +" montoLimite"+comision.getMontoLimite()+ "'");
    }


    @Override
    public void modificarComisionPorOrganizacion(DTOComisionPorOrganizacion dto) throws Exception {

        LocalDateTime fechaDesde = Instant.ofEpochMilli(dto.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime fechaHasta = Instant.ofEpochMilli(dto.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime hoy = LocalDateTime.now();

        if (comisionPorOrganizacionRepository
                .existsVigenteWithSameMonto(dto.getMontoLimite(), hoy, dto.getId())) {
            throw new Exception("Ya existe otra comisión vigente con el mismo monto límite.");
        }

        comisionPorOrganizacionRepository.update(
                dto.getId(),
                dto.getMontoLimite(),
                dto.getPorcentaje(),
                fechaDesde,
                fechaHasta
        );
        registroSingleton.write("Parametros", "comision_organizacion", "modificacion", "ComisionOrganizacion de ID " + dto.getId() + " montoLimite '" + dto.getMontoLimite() + "'");
    }


    @Override
    public void bajaComisionPorOrganizacion(Long id) throws Exception {
        comisionPorOrganizacionRepository.delete(id, LocalDateTime.now());
        registroSingleton.write("Parametros", "comision_organizacion", "eliminacion", "ComisionOrganizacion de ID " + id + "'");
    }

    @Override
    public void restaurarComisionPorOrganizacion(Long id) throws Exception {
        ComisionPorOrganizacion comisionPorOrganizacion = comisionPorOrganizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comisión por organización no encontrada"));

        LocalDateTime hoy = LocalDateTime.now();

        if (comisionPorOrganizacionRepository
                .existsVigenteWithSameMonto(comisionPorOrganizacion.getMontoLimite(), hoy, id)) {
            throw new Exception("Ya existe otra comisión vigente con el mismo monto límite.");
        }
        comisionPorOrganizacion.setFechaHasta(null);
        if(comisionPorOrganizacion.getFechaDesde().isAfter(LocalDateTime.now()))
            comisionPorOrganizacion.setFechaDesde(LocalDateTime.now());
        this.save(comisionPorOrganizacion);
        registroSingleton.write("Parametros", "comision_organizacion", "restauracion", "ComisionOrganizacion de ID " + id + "'");
    }
    
}
