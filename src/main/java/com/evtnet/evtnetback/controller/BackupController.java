package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.backup.DTOBackup;
import com.evtnet.evtnetback.dto.backup.DTOProgramacionBackupsAutomaticos;
import com.evtnet.evtnetback.entity.ProgramacionBackup;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.service.BackupServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backups")
@AllArgsConstructor
public class BackupController extends BaseControllerImpl <ProgramacionBackup, BackupServiceImpl>{
    private final BackupServiceImpl backupService;

    @GetMapping("/obtenerBackups")
    public ResponseEntity obtenerBackups(@RequestParam(name="page", defaultValue = "0")int page){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(backupService.listarBackups(page));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener los backups - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerProgramacion")
    public ResponseEntity obtenerProgramacion(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(backupService.obtenerProgramacionActivaDTO());
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la última programación - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/programarCopiaManual")
    public ResponseEntity programarCopiaManual(@RequestParam(name="fechaHora")Long fechaHora){
        try{
            backupService.crearBackupManual(fechaHora);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener crear la copia manual - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/programarCopiasAutomaticas")
    public ResponseEntity programarCopiasAutomaticas(@RequestBody DTOProgramacionBackupsAutomaticos dto){
        try{
            backupService.programarAutomatica(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener programar la copia automática - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/eliminarCopia")
    public ResponseEntity eliminarCopia(@RequestBody DTOBackup backup){
        try{
            backupService.eliminarBackup(backup);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo eliminar la copia - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


}
