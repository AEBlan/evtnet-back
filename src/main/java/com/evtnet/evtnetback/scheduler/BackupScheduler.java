package com.evtnet.evtnetback.scheduler;

import com.evtnet.evtnetback.entity.ProgramacionBackup;
import com.evtnet.evtnetback.executor.BackupExecutor;
import com.evtnet.evtnetback.service.BackupService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class BackupScheduler {

    private final TaskScheduler scheduler;
    private final BackupService backupService;
    private final BackupExecutor executor;
    private Runnable tareaProgramada;

    public BackupScheduler(@Qualifier("taskScheduler") TaskScheduler scheduler,
                           BackupService backupService,
                           BackupExecutor executor) {
        this.scheduler = scheduler;
        this.backupService = backupService;
        this.executor = executor;
    }

    @PostConstruct
    public void init() {
        try {
            executor.ejecutarManualesPendientes();

            ProgramacionBackup prog = backupService.obtenerProgramacionActiva();
            if (prog != null) {
                executor.evaluarYEjecutarAutomaticosPendientes(prog);
                executor.aplicarRetencion(prog.getCopiasAConservar());
            }

            programarSiguienteEjecucion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedDelay = 60000) // cada 30 segundos
    public void verificarPeriodicamente() {
        try {
            System.out.println("[SCHEDULER] Revisión periódica...");

            executor.ejecutarManualesPendientes();

            ProgramacionBackup prog = backupService.obtenerProgramacionActiva();
            if (prog == null) return;

            executor.evaluarYEjecutarAutomaticosPendientes(prog);

            executor.aplicarRetencion(prog.getCopiasAConservar());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void programarSiguienteEjecucion() {

        ProgramacionBackup prog = backupService.obtenerProgramacionActiva();
        if (prog == null) return;

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime proxima = prog.getFechaDesde();

        while (!proxima.isAfter(ahora)) {
            if (prog.getMeses() > 0)
                proxima = proxima.plusMonths(prog.getMeses());
            if (prog.getDias() > 0)
                proxima = proxima.plusDays(prog.getDias());
            if (prog.getHoras() > 0)
                proxima = proxima.plusHours(prog.getHoras());
        }

        tareaProgramada = () -> {
            try {
                executor.evaluarYEjecutarAutomaticosPendientes(prog);
                executor.aplicarRetencion(prog.getCopiasAConservar());
                programarSiguienteEjecucion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        Date fecha = Date.from(proxima.atZone(ZoneId.systemDefault()).toInstant());
        scheduler.schedule(tareaProgramada, fecha);
    }
}

