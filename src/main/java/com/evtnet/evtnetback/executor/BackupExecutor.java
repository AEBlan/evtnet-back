package com.evtnet.evtnetback.executor;

import com.evtnet.evtnetback.entity.ProgramacionBackup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class BackupExecutor {

    @Value("${app.backup.directory}")
    private String backupDirectory;

    @Value("${app.db.container}")
    private String dbContainer;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    public void ejecutarManualesPendientes() throws IOException {
        Path base = Paths.get(backupDirectory);
        if (!Files.exists(base)) return;

        List<Path> pendientes;
        try (Stream<Path> st = Files.list(base)) {
            pendientes = st
                    .filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("manual_pendiente"))
                    .filter(p -> extraerFechaDesdeNombre(p).isBefore(LocalDateTime.now()))
                    .toList();
        }

        if (pendientes.isEmpty()) return;

        Path masReciente = pendientes.stream()
                .max(Comparator.comparing(this::extraerFechaDesdeNombre))
                .orElseThrow();

        for (Path p : pendientes) {
            if (!p.equals(masReciente)) borrarCarpeta(p);
        }

        ejecutarManual(masReciente);
    }

    public void evaluarYEjecutarAutomaticosPendientes(ProgramacionBackup programacionBackup) throws IOException {
        if (!programacionBackup.isActiva()) return;

        LocalDateTime ahora = LocalDateTime.now();

        if (ahora.isBefore(programacionBackup.getFechaDesde()) ||
                (programacionBackup.getFechaHoraBaja() != null && ahora.isAfter(programacionBackup.getFechaHoraBaja()))) {
            return;
        }

        Path base = Paths.get(backupDirectory);
        if (!Files.exists(base)) return;

        Optional<Path> ultimaCompleta = obtenerUltimaCompleta(programacionBackup.getId());
        Optional<Path> ultimoIncremental = obtenerUltimoIncremental(programacionBackup.getId());

        int countIncrementales = ultimoIncremental.map(this::extraerId).orElse(0)
                - ultimaCompleta.map(this::extraerId).orElse(0);

        long totalHorasFrecuencia = programacionBackup.getMeses() * 30 * 24L
                + programacionBackup.getDias() * 24L
                + programacionBackup.getHoras();

        boolean correspondeCompleta = ultimaCompleta.isEmpty() || countIncrementales >= programacionBackup.getCopiasIncrementales();

        LocalDateTime fechaUltimaCopia;
        long horasTranscurridas;
        boolean correspondePorTiempo;

        if (correspondeCompleta) {
            if(ultimaCompleta.isEmpty()) {
                if (programacionBackup.getFechaDesde().isEqual(ahora.truncatedTo(ChronoUnit.MINUTES)) || programacionBackup.getFechaDesde().isBefore(ahora.truncatedTo(ChronoUnit.MINUTES)))
                    ejecutarCopiaAutomaticaCompleta(programacionBackup.getId());
            }else{
                fechaUltimaCopia = extraerFechaDesdeNombre(ultimaCompleta.get());
                horasTranscurridas = java.time.Duration.between(fechaUltimaCopia, ahora).toHours();
                correspondePorTiempo = programacionBackup.getFechaDesde().isEqual(ahora.truncatedTo(ChronoUnit.MINUTES)) || horasTranscurridas >= totalHorasFrecuencia;
                if (correspondePorTiempo) ejecutarCopiaAutomaticaCompleta(programacionBackup.getId());
            }
        }
        else {
            if(ultimoIncremental.isEmpty()) {
                LocalDateTime fechaReferencia = programacionBackup.getFechaDesde().plusHours(totalHorasFrecuencia);
                if (programacionBackup.getFechaDesde().isEqual(ahora.truncatedTo(ChronoUnit.MINUTES)) || fechaReferencia.isBefore(ahora.truncatedTo(ChronoUnit.MINUTES)))
                    ejecutarCopiaAutomaticaIncremental(ultimaCompleta.orElse(null), programacionBackup.getId());
            }else{
                fechaUltimaCopia = extraerFechaDesdeNombre(ultimoIncremental.get());
                horasTranscurridas = java.time.Duration.between(fechaUltimaCopia, ahora).toHours();
                correspondePorTiempo = programacionBackup.getFechaDesde().isEqual(ahora.truncatedTo(ChronoUnit.MINUTES)) || horasTranscurridas >= totalHorasFrecuencia;
                if (correspondePorTiempo) ejecutarCopiaAutomaticaIncremental(ultimaCompleta.orElse(null), programacionBackup.getId());
            }
        }
    }

    private Optional<Path> obtenerUltimaCompleta(Long idProgramacionBackup) throws IOException {
        Path base = Paths.get(backupDirectory);
        try (Stream<Path> st = Files.list(base)) {
            return st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_comp"))
                    .filter(p -> {
                        String[] partes = p.getFileName().toString().split("_");
                        return partes.length > 2 && Long.parseLong(partes[2]) == idProgramacionBackup;
                    })
                    .max(Comparator.comparing(this::extraerFechaDesdeNombre));
        }
    }

    private Optional<Path> obtenerUltimoIncremental(Long idProgramacionBackup) throws IOException {
        Path base = Paths.get(backupDirectory);
        try (Stream<Path> st = Files.list(base)) {
            return st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_inc"))
                    .filter(p -> {
                        String[] partes = p.getFileName().toString().split("_");
                        return partes.length > 2 && Long.parseLong(partes[2]) == idProgramacionBackup;
                    })
                    .max(Comparator.comparing(this::extraerFechaDesdeNombre));
        }
    }


    public void ejecutarManual(Path carpetaPendiente) throws IOException {
        try {
            ejecutarMariabackup(carpetaPendiente, null);
            renombrarManualPendiente(carpetaPendiente);
        } catch (Exception e) {
            System.out.print(e.getMessage());
         }
    }

    private void ejecutarCopiaAutomaticaCompleta(Long idProgramacionBackup) throws IOException {
        int id = calcularSiguienteId();
        String tmpName = "backup_"+id+"_" + idProgramacionBackup + "_tmp";
        Path tmp = Paths.get(backupDirectory, tmpName);
        Files.createDirectories(tmp);

        try {
            ejecutarMariabackup(tmp, null);
            String finalName = generarNombreCompleta(id, idProgramacionBackup);
            Files.move(tmp, tmp.resolveSibling(finalName), StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    private void ejecutarCopiaAutomaticaIncremental(Path completaBase, Long idProgramacionBackup) throws IOException {
        int id = calcularSiguienteId();
        int dependeDe = extraerId(completaBase);
        String tmpName = "backup_" + id + "_" + idProgramacionBackup + "_tmp";
        Path tmp = Paths.get(backupDirectory, tmpName);
        Files.createDirectories(tmp);

        try {
            ejecutarMariabackup(tmp, completaBase);
            String finalName = generarNombreIncremental(id, dependeDe, idProgramacionBackup);
            Files.move(tmp, tmp.resolveSibling(finalName), StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }


    }

    private void ejecutarMariabackup(Path destino, Path base) throws IOException, InterruptedException {

        String container = dbContainer;
        //String containerLocal = dbContainer+"_local";

        String carpeta = destino.getFileName().toString();
        //docker exec evtnet-mariadb mariadb-backup --backup --target-dir=/tmp/backup --user=root --password=root_pass
        String tempBackupDir = "/tmp/backup";

        List<String> limpiarCmd = List.of(
            "docker", "exec", container,
            "sh", "-c", "rm -rf " + tempBackupDir + "/*"
        );
        new ProcessBuilder(limpiarCmd).inheritIO().start().waitFor();

        List<String> mkdirCmd = List.of(
                "docker", "exec", container,
                "sh", "-c", "mkdir -p " + tempBackupDir
        );
        new ProcessBuilder(mkdirCmd).inheritIO().start().waitFor();

        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("exec");
        cmd.add(container);
        cmd.add("mariadb-backup");
        cmd.add("--backup");

        cmd.add("--target-dir=" + tempBackupDir);

        cmd.add("--user=root");
        cmd.add("--password=root_pass");

        new ProcessBuilder(cmd).inheritIO().start().waitFor();

        String destinoHost = backupDirectory + "/" + carpeta;
        new File(destinoHost).mkdirs();

        //docker cp evtnet-mariadb:/tmp/backup/. "%BACKUP_DIR%/"
        List<String> copyCmd = List.of(
            "docker", "cp",
            container + ":" + tempBackupDir + "/.",
            destinoHost
        );
        new ProcessBuilder(copyCmd).inheritIO().start().waitFor();
    }

    public void aplicarRetencion(int conservar) throws IOException {
        Path base = Paths.get(backupDirectory);
        if (!Files.exists(base)) return;

        List<Path> completas;
        try (Stream<Path> st = Files.list(base)) {
            completas = st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_comp"))
                    .sorted(Comparator.comparing(this::extraerFechaDesdeNombre).reversed())
                    .toList();
        }

        List<Path> conservarLista = completas.stream().limit(conservar + 1).toList();

        for (Path comp : completas) {
            if (!conservarLista.contains(comp)) {
                borrarCadenaBackup(comp);
            }
        }
    }

    private void borrarCadenaBackup(Path completa) throws IOException {
        Path base = Paths.get(backupDirectory);
        List<Path> relacionados = new ArrayList<>();
        relacionados.add(completa);

        try (Stream<Path> st = Files.list(base)) {
            st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_inc"))
                    .filter(p -> extraerDependencia(p) == extraerId(completa))
                    .forEach(relacionados::add);
        }

        for (Path p : relacionados) borrarCarpeta(p);
    }

    private int extraerDependencia(Path p) {
        String[] partes = p.getFileName().toString().split("_");
        return Integer.parseInt(partes[5]);
    }

    private String generarNombreCompleta(int id, Long idProgramacionBackup) {
        String fecha = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        return "backup_" + id + "_" + idProgramacionBackup + "_auto_comp_" + fecha;
    }

    private String generarNombreIncremental(int id, int dependeDe, Long idProgramacionBackup) {
        String fecha = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        return "backup_" + id + "_" + idProgramacionBackup + "_auto_inc_" + dependeDe + "_" + fecha;
    }

    private int calcularSiguienteId() {
        Path base = Paths.get(backupDirectory);
        try (Stream<Path> st = Files.list(base)) {
            return st.filter(Files::isDirectory)
                    .map(p -> extraerId(p))
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
        } catch (IOException e) {
            return 1;
        }
    }

    private int extraerId(Path p) {
        String[] partes = p.getFileName().toString().split("_");
        return Integer.parseInt(partes[1]);
    }

    private LocalDateTime extraerFechaDesdeNombre(Path p) {
        String[] partes = p.getFileName().toString().split("_");
        String fecha = partes[partes.length - 2];
        String hora = partes[partes.length - 1];
        return LocalDateTime.parse(fecha + "T" + hora.replace("-", ":"));
    }

    private void renombrarManualPendiente(Path carpeta) throws IOException {
        String nombre = carpeta.getFileName().toString();
        String nuevo = nombre.replace("_pendiente", "");
        Files.move(carpeta, carpeta.resolveSibling(nuevo), StandardCopyOption.ATOMIC_MOVE);
    }

    private void borrarCarpeta(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
