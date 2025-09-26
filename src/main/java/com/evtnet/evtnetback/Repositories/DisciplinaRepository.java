package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Disciplina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaRepository extends BaseRepository <Disciplina, Long> {}
