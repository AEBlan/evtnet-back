package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ParametroSistema;
import com.evtnet.evtnetback.Entities.ParametroSistema;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.ParametroSistemaRepository;
import com.evtnet.evtnetback.dto.parametroSistema.DTOParametroSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ParametroSistemaServiceImpl extends BaseServiceImpl <ParametroSistema, Long> implements ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;
    
    public ParametroSistemaServiceImpl(ParametroSistemaRepository parametroSistemaRepository) {
        super(parametroSistemaRepository);
        this.parametroSistemaRepository = parametroSistemaRepository;
    }

    @Override
    public Page<DTOParametroSistema> obtenerListaParametroSistema(Pageable pageable) throws Exception {
//        Specification<ParametroSistema> spec = Specification.where(null);
        Specification<ParametroSistema> spec = (root, query, cb) ->
                cb.isNull(root.get("fechaHoraBaja"));
        Page<ParametroSistema> parametrosSistema = parametroSistemaRepository.findAll(spec, pageable);
        return parametrosSistema
                .map(me->DTOParametroSistema.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .valor(me.getValor())
                        .build()
                );
    }

    @Override
    public DTOParametroSistema obtenerParametroSistemaCompleto(Long id) throws Exception {
        ParametroSistema parametroSistema = parametroSistemaRepository.findById(id).get();
        return DTOParametroSistema.builder()
                .id(parametroSistema.getId())
                .nombre(parametroSistema.getNombre())
                .valor(parametroSistema.getValor())
                .build();
    }

    @Override
    public void altaParametroSistema(DTOParametroSistema parametroSistema) throws Exception {
        this.save(ParametroSistema.builder()
                .nombre(parametroSistema.getNombre())
                .valor(parametroSistema.getValor())
                .build());
    }

    @Override
    public void modificarParametroSistema(DTOParametroSistema ParametroSistema) throws Exception {
        parametroSistemaRepository.update(ParametroSistema.getId(), ParametroSistema.getNombre(), ParametroSistema.getValor());
    }

    @Override
    public void bajaParametroSistema(Long id) throws Exception {
        parametroSistemaRepository.delete(id, LocalDateTime.now());
    }
    
}
