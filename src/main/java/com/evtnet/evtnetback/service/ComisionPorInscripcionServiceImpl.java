package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComisionPorInscripcion;
import com.evtnet.evtnetback.repository.ComisionPorInscripcionRepository;
import com.evtnet.evtnetback.dto.comisionPorInscripcion.DTOComisionPorInscripcion;
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
public class ComisionPorInscripcionServiceImpl extends BaseServiceImpl <ComisionPorInscripcion, Long> implements ComisionPorInscripcionService {

    private final ComisionPorInscripcionRepository comisionPorInscripcionRepository;
    private final ParametroSistemaService parametroSistemaService;
    private final RegistroSingleton registroSingleton;
    
    public ComisionPorInscripcionServiceImpl(ComisionPorInscripcionRepository comisionPorInscripcionRepository,
                                             ParametroSistemaService parametroSistemaService,
                                             RegistroSingleton registroSingleton) {
        super(comisionPorInscripcionRepository);
        this.comisionPorInscripcionRepository = comisionPorInscripcionRepository;
        this.parametroSistemaService = parametroSistemaService;
        this.registroSingleton = registroSingleton;
    }

    @Override
    public Page<DTOComisionPorInscripcion> obtenerListaComisionPorInscripcion(int page, boolean activas, boolean noActivas) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHasta"),
                        Sort.Order.asc("fechaDesde")
                )
        );
        Specification<ComisionPorInscripcion> spec = Specification.where(null);
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
        ComisionPorInscripcion comisionPorInscripcion = comisionPorInscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comisión por inscripción no encontrada"));
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
    public void altaComisionPorInscripcion(DTOComisionPorInscripcion dto) throws Exception {

        LocalDateTime fechaDesde = Instant.ofEpochMilli(dto.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime fechaHasta = Instant.ofEpochMilli(dto.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (comisionPorInscripcionRepository.existsMontoLimiteVigente(
                dto.getMontoLimite(),
                null)) {
            throw new Exception("Ya existe una comisión por inscripción con el mismo monto límite vigente en ese período.");
        }

        ComisionPorInscripcion comision=this.save(ComisionPorInscripcion.builder()
                .montoLimite(dto.getMontoLimite())
                .porcentaje(dto.getPorcentaje())
                .fechaDesde(fechaDesde)
                .fechaHasta(fechaHasta)
                .build());

        registroSingleton.write("Parametros", "comision_inscripcion", "creacion", "ComisionInscripcion de ID " + comision.getId() + " montoLimite '" + comision.getMontoLimite() + "'");
    }


    @Override
    public void modificarComisionPorInscripcion(DTOComisionPorInscripcion dto) throws Exception {

        LocalDateTime fechaDesde = Instant.ofEpochMilli(dto.getFechaDesde())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime fechaHasta = Instant.ofEpochMilli(dto.getFechaHasta())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (comisionPorInscripcionRepository.existsMontoLimiteVigente(
                dto.getMontoLimite(),
                dto.getId() )) {
            throw new Exception("Ya existe otra comisión por inscripción con el mismo monto límite vigente en ese período.");
        }

        comisionPorInscripcionRepository.update(
                dto.getId(),
                dto.getMontoLimite(),
                dto.getPorcentaje(),
                fechaDesde,
                fechaHasta
        );
        registroSingleton.write("Parametros", "comision_inscripcion", "modificacion", "ComisionInscripcion de ID " + dto.getId() + " montoLimite '" + dto.getMontoLimite() + "'");
    }


    @Override
    public void bajaComisionPorInscripcion(Long id) throws Exception {
        comisionPorInscripcionRepository.delete(id, LocalDateTime.now());
        registroSingleton.write("Parametros", "comision_inscripcion", "eliminacion", "ComisionInscripcion de ID " + id + "'");
    }

    @Override
    public void restaurarComisionPorInscripcion(Long id) throws Exception {
        ComisionPorInscripcion comisionPorInscripcion = comisionPorInscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comisión por inscripción no encontrada"));
        if (comisionPorInscripcionRepository.existsMontoLimiteVigente(
                comisionPorInscripcion.getMontoLimite(),
                comisionPorInscripcion.getId() )) {
            throw new Exception("Ya existe otra comisión por inscripción con el mismo monto límite vigente en ese período.");
        }
        comisionPorInscripcion.setFechaHasta(null);
        if(comisionPorInscripcion.getFechaDesde().isAfter(LocalDateTime.now()))
            comisionPorInscripcion.setFechaDesde(LocalDateTime.now());
        this.save(comisionPorInscripcion);
        registroSingleton.write("Parametros", "comision_inscripcion", "restauracion", "ComisionInscripcion de ID " + id + "'");
    }
}
