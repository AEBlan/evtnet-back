package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.entity.InstanciaMascotaSecuencia;

import java.util.List;

public interface InstanciaMascotaSecuenciaRepository extends BaseRepository <InstanciaMascotaSecuencia, Long> {
    List<InstanciaMascotaSecuencia> findByInstanciaMascotaAndFechaHoraBajaIsNullOrderByOrdenAsc(InstanciaMascota instancia);
}
