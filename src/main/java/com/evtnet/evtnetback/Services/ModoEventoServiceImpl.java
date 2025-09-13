package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Repositories.ModoEventoRepository;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import org.springframework.stereotype.Service;
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
                .map(me -> new DTOModoEvento(me.getId(), me.getNombre()))
                .toList();
    }
}
