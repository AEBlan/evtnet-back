package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ProgramacionBackup;

import java.util.Optional;

public interface ProgramacionBackupRepository extends BaseRepository <ProgramacionBackup, Long>{
    Optional<ProgramacionBackup> findByActivaTrue();
    ProgramacionBackup findTopByActivaTrueOrderByFechaHoraAltaDesc();
}
