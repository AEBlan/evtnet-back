package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ProgramacionBackup;
import com.evtnet.evtnetback.dto.backup.DTOBackup;
import com.evtnet.evtnetback.dto.backup.DTOProgramacionBackupsAutomaticos;
import org.springframework.data.domain.Page;

public interface BackupService extends BaseService <ProgramacionBackup, Long>{
    Page<DTOBackup> listarBackups(int page) throws Exception;
    void crearBackupManual(Long fechaHora) throws Exception;
    void eliminarBackup(DTOBackup dto) throws Exception;
    ProgramacionBackup obtenerProgramacionActiva();
    void programarAutomatica(DTOProgramacionBackupsAutomaticos dto) throws Exception;
    DTOProgramacionBackupsAutomaticos obtenerProgramacionActivaDTO()throws Exception;
}
