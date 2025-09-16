package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.dto.grupos.DTOGrupoSimple;
import com.evtnet.evtnetback.Entities.Grupo;
import com.evtnet.evtnetback.Repositories.GrupoRepository;
import com.evtnet.evtnetback.Services.GrupoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GrupoServiceImpl extends BaseServiceImpl <Grupo, Long> implements GrupoService {

    private final GrupoRepository grupoRepo;
    public GrupoServiceImpl(GrupoRepository grupoRepo) {
        super(grupoRepo);
        this.grupoRepo = grupoRepo;
    }

    @Override
    public Page<DTOGrupoSimple> obtenerGrupos(String texto, int page) {
        var pageable = PageRequest.of(page, 10); // tamaño fijo de 10 por ejemplo
        var grupos = grupoRepo.buscarPorTexto(texto, pageable);

        return grupos.map(g -> DTOGrupoSimple.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .fechaBaja(g.getFechaHoraBaja())
                .creador(DTOGrupoSimple.CreadorDTO.builder()
                        .nombre(g.getUsuariosGrupo().isEmpty() ? "?" : g.getUsuariosGrupo().get(0).getUsuario().getNombre())
                        .apellido(g.getUsuariosGrupo().isEmpty() ? "" : g.getUsuariosGrupo().get(0).getUsuario().getApellido())
                        .build())
                .build());
    }
    
}
