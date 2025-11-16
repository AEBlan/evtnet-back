package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.entity.Usuario;
import com.evtnet.evtnetback.entity.UsuarioInstanciaMascota;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioInstanciaMascotaRepository extends BaseRepository <UsuarioInstanciaMascota, Long> {
    boolean existsByUsuarioAndInstanciaMascota(Usuario usuario, InstanciaMascota instanciaMascota);
}
