package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class DisciplinaServiceImpl extends BaseServiceImpl <Disciplina, Long> implements DisciplinaService {

    public DisciplinaServiceImpl(BaseRepository<Disciplina, Long> baseRepository) {
        super(baseRepository);
    }
    
}
