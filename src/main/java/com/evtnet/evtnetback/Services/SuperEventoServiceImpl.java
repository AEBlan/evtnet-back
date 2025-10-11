package com.evtnet.evtnetback.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.SuperEventoRepository;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaAdministrados;
import com.evtnet.evtnetback.util.CurrentUser;

@Service
public class SuperEventoServiceImpl extends BaseServiceImpl <SuperEvento,Long> implements SuperEventoService  {

    private final SuperEventoRepository repo;

    public SuperEventoServiceImpl(BaseRepository<SuperEvento, Long> baseRepository, SuperEventoRepository repo) {
        super(baseRepository);
        this.repo = repo;
    }

    @Override
    public List<DTOBusquedaAdministrados> buscarAdministrados(String text) throws Exception {
        
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No se encontró al usuario"));
        
        List<SuperEvento> supereventos = repo.searchByUsuario_Username(username, text);

        ArrayList<DTOBusquedaAdministrados> ret = new ArrayList<>();

        supereventos.forEach(s -> {
            ret.add(DTOBusquedaAdministrados.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .build());
        });

        return ret;
    }
    
}
