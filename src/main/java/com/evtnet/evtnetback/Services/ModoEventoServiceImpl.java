package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Repositories.ModoEventoRepository;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ModoEventoServiceImpl extends BaseServiceImpl<ModoEvento, Long> implements ModoEventoService {

    private final ModoEventoRepository modoEventoRepository;

    public ModoEventoServiceImpl(ModoEventoRepository modoEventoRepository) {
        super(modoEventoRepository);               
        this.modoEventoRepository = modoEventoRepository;
    }
    @Override
    public List<DTOModoEvento> buscarPorNombre(String text) throws Exception {
        String q = text == null ? "" : text.trim().toLowerCase();
        return findAll().stream()
                .filter(me -> me.getNombre() != null)
                .filter(me -> q.isEmpty() || me.getNombre().toLowerCase().contains(q))
                .map(me -> DTOModoEvento.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .build())
                .toList();
    }

    @Override
    public Page<DTOModoEvento> obtenerListaModosEvento(Pageable pageable) throws Exception {
        Specification<ModoEvento> spec = Specification.where(null);
        Page<ModoEvento> modosEvento = modoEventoRepository.findAll(spec, pageable);
        return modosEvento
                .map(me->DTOModoEvento.builder()
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
    public DTOModoEvento obtenerModoEventoCompleto(Long id) throws Exception {
        ModoEvento modoEvento = modoEventoRepository.findById(id).get();
        return DTOModoEvento.builder()
                .id(modoEvento.getId())
                .nombre(modoEvento.getNombre())
                .descripcion(modoEvento.getDescripcion())
                .build();
    }

    @Override
    public void altaModoEvento(DTOModoEvento modoEvento) throws Exception {
        this.save(ModoEvento.builder()
                        .nombre(modoEvento.getNombre())
                        .descripcion(modoEvento.getDescripcion())
                        .fechaHoraAlta(LocalDateTime.now())
                .build());
    }

    @Override
    public void modificarModoEvento(DTOModoEvento modoEvento) throws Exception {
        modoEventoRepository.update(modoEvento.getId(), modoEvento.getNombre(), modoEvento.getDescripcion());
    }

    @Override
    public void bajaModoEvento(Long id) throws Exception {
        modoEventoRepository.delete(id, LocalDateTime.now());
    }


}
