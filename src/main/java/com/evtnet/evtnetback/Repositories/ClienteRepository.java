package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, Long> {
}
