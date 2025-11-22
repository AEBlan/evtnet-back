package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ParametroSistema;
import com.evtnet.evtnetback.repository.ParametroSistemaRepository;
import com.evtnet.evtnetback.dto.parametroSistema.DTOParametroSistema;
import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


import java.time.LocalDateTime;

@Service
public class ParametroSistemaServiceImpl extends BaseServiceImpl <ParametroSistema, Long> implements ParametroSistemaService {

    private final ParametroSistemaRepository parametroSistemaRepository;
    private final RegistroSingleton registroSingleton;
    
    public ParametroSistemaServiceImpl(ParametroSistemaRepository parametroSistemaRepository,
                                       RegistroSingleton registroSingleton) {
        super(parametroSistemaRepository);
        this.parametroSistemaRepository = parametroSistemaRepository;
        this.registroSingleton = registroSingleton;
    }

    @Override
    public Page<DTOParametroSistema> obtenerListaParametroSistema(int page) throws Exception {
        Integer longitudPagina = getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina
        );
        Specification<ParametroSistema> spec = (root, query, cb) ->
                cb.isNull(root.get("fechaHoraBaja"));
        Page<ParametroSistema> parametrosSistema = parametroSistemaRepository.findAll(spec, pageable);
        return parametrosSistema
                .map(me->DTOParametroSistema.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .valor(me.getValor())
                        .descripcion(me.getDescripcion())
                        .identificador(me.getIdentificador())
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
    public void modificarParametroSistema(DTOParametroSistema parametroSistema) throws Exception {
        parametroSistemaRepository.update(parametroSistema.getId(), parametroSistema.getNombre(), parametroSistema.getValor());
        registroSingleton.write("Parametros", "parametro", "modificacion", "ParametroSistema de ID " + parametroSistema.getId());
    }

    @Override
    public void bajaParametroSistema(Long id) throws Exception {
        parametroSistemaRepository.delete(id, LocalDateTime.now());
    }

    public BigDecimal getDecimal(String key, BigDecimal def) {
        return parametroSistemaRepository.findByIdentificador(key)
                   .map(p -> new BigDecimal(p.getValor()))
                   .orElse(def);
    }
    
    public Integer getInt(String key, Integer def) {
        return parametroSistemaRepository.findByIdentificador(key)
                   .map(p -> Integer.parseInt(p.getValor()))
                   .orElse(def);
    }
    
    
}
