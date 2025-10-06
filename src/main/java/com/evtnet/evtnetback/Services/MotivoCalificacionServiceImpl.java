package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.MotivoCalificacion;
import com.evtnet.evtnetback.Entities.TipoCalificacion;
import com.evtnet.evtnetback.Repositories.MotivoCalificacionRepository;
import com.evtnet.evtnetback.dto.motivoCalificacion.DTOMotivoCalificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MotivoCalificacionServiceImpl extends BaseServiceImpl <MotivoCalificacion, Long> implements MotivoCalificacionService {

    private final MotivoCalificacionRepository motivoCalificacionRepository;
    private final TipoCalificacionServiceImpl tipoCalificacionService;
    
    public MotivoCalificacionServiceImpl(MotivoCalificacionRepository motivoCalificacionRepository, TipoCalificacionServiceImpl tipoCalificacionService) {
        super(motivoCalificacionRepository);
        this.motivoCalificacionRepository = motivoCalificacionRepository;
        this.tipoCalificacionService = tipoCalificacionService;
    }

    @Override
    public Page<DTOMotivoCalificacion> obtenerListaMotivoCalificacion(Pageable pageable) throws Exception {
        Specification<MotivoCalificacion> spec = Specification.where(null);
        Page<MotivoCalificacion> motivosCalificacion = motivoCalificacionRepository.findAll(spec, pageable);
        return motivosCalificacion
                .map(ic-> DTOMotivoCalificacion.builder()
                        .id(ic.getId())
                        .nombre(ic.getNombre())
                        .idTipoCalificacion(ic.getTipoCalificacion().getId())
                        .nombreTipoCalificacion(ic.getTipoCalificacion().getNombre())
                        .build()
                );
    }

    @Override
    public DTOMotivoCalificacion obtenerMotivoCalificacionCompleto(Long id) throws Exception {
        MotivoCalificacion motivoCalificacion = motivoCalificacionRepository.findById(id).get();
//        MotivoCalificacion motivoCalificacion = motivoCalificacionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Icono no encontrado"));
        return DTOMotivoCalificacion.builder()
                .id(motivoCalificacion.getId())
                .nombre(motivoCalificacion.getNombre())
                .idTipoCalificacion(motivoCalificacion.getTipoCalificacion().getId())
                .nombreTipoCalificacion(motivoCalificacion.getTipoCalificacion().getNombre())
                .build();
    }

    @Override
    public void altaMotivoCalificacion(DTOMotivoCalificacion motivoCalificacion) throws Exception {
        TipoCalificacion tipoCalificacion=tipoCalificacionService.findById(motivoCalificacion.getIdTipoCalificacion());
        this.save(MotivoCalificacion.builder()
                .nombre(motivoCalificacion.getNombre())
                .tipoCalificacion(tipoCalificacion)
                .build());
    }

    @Override
    public void modificarMotivoCalificacion(DTOMotivoCalificacion motivoCalificacion) throws Exception {
        TipoCalificacion tipoCalificacion=tipoCalificacionService.findById(motivoCalificacion.getIdTipoCalificacion());
        motivoCalificacionRepository.update(motivoCalificacion.getId(), tipoCalificacion, motivoCalificacion.getNombre());
    }

    @Override
    public void bajaMotivoCalificacion(Long id) throws Exception {
        motivoCalificacionRepository.delete(id, LocalDateTime.now());
    }
    
}
