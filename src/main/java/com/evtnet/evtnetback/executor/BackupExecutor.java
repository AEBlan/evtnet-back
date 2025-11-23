package com.evtnet.evtnetback.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
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

    public void evaluarYEjecutarAutomaticosPendientes(int incrementalesPorCompleta) throws IOException {
        Path base = Paths.get(backupDirectory);
        if (!Files.exists(base)) return;

        Optional<Path> ultimaCompleta = obtenerUltimaCompleta();
        Optional<Path> ultimoIncremental = obtenerUltimoIncremental();

        int countIncrementales = ultimoIncremental.map(this::extraerId).orElse(0)
                - ultimaCompleta.map(this::extraerId).orElse(0);

        boolean correspondeCompleta = ultimaCompleta.isEmpty() || countIncrementales >= incrementalesPorCompleta;

        if (correspondeCompleta) ejecutarCopiaAutomaticaCompleta();
        else ejecutarCopiaAutomaticaIncremental(ultimaCompleta.orElse(null));
    }

    private Optional<Path> obtenerUltimaCompleta() throws IOException {
        Path base = Paths.get(backupDirectory);
        try (Stream<Path> st = Files.list(base)) {
            return st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_comp"))
                    .max(Comparator.comparing(this::extraerFechaDesdeNombre));
        }
    }

    private Optional<Path> obtenerUltimoIncremental() throws IOException {
        Path base = Paths.get(backupDirectory);
        try (Stream<Path> st = Files.list(base)) {
            return st.filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().contains("auto_inc"))
                    .max(Comparator.comparing(this::extraerFechaDesdeNombre));
        }
    }

    public void ejecutarManual(Path carpetaPendiente) throws IOException {
        Path tmp = carpetaPendiente.resolve("tmp");
        Files.createDirectories(tmp);

        try {
            ejecutarMariabackup(tmp, null);
        } catch (Exception e) { }

        renombrarManualPendiente(carpetaPendiente);
    }

    private void ejecutarCopiaAutomaticaCompleta() throws IOException {
        int id = calcularSiguienteId();
        String tmpName = "backup_" + id + "_tmp";
        Path tmp = Paths.get(backupDirectory, tmpName);
        Files.createDirectories(tmp);

        try {
            ejecutarMariabackup(tmp, null);
        } catch (Exception e) { }

        String finalName = generarNombreCompleta(id);
        Files.move(tmp, tmp.resolveSibling(finalName), StandardCopyOption.ATOMIC_MOVE);
    }

    private void ejecutarCopiaAutomaticaIncremental(Path completaBase) throws IOException {
        int id = calcularSiguienteId();
        int dependeDe = extraerId(completaBase);
        String tmpName = "backup_" + id + "_tmp";
        Path tmp = Paths.get(backupDirectory, tmpName);
        Files.createDirectories(tmp);

        try {
            ejecutarMariabackup(tmp, completaBase);
        } catch (Exception e) { }

        String finalName = generarNombreIncremental(id, dependeDe);
        Files.move(tmp, tmp.resolveSibling(finalName), StandardCopyOption.ATOMIC_MOVE);
    }

    private void ejecutarMariabackup(Path destino, Path base) throws Exception {

        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("exec");
        cmd.add(dbContainer);

        cmd.add("mariadb-backup");

        cmd.add("--backup");
        cmd.add("--target-dir=" + "/backups/" + destino.getFileName() + "/data");

        if (base != null) {
            cmd.add("--incremental-basedir=" + "/backups/" + base.getFileName() + "/data");
        }

        cmd.add("--user=" + dbUser);
        cmd.add("--password=" + dbPass);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[BACKUP] " + line);
            }
        }

        int exit = p.waitFor();
        if (exit != 0) {
            throw new RuntimeException("Error ejecutando mariadb-backup. Exit code = " + exit);
        }
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
        return Integer.parseInt(partes[3]);
    }

    private String generarNombreCompleta(int id) {
        String fecha = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        return "backup_" + id + "_auto_comp_" + fecha;
    }

    private String generarNombreIncremental(int id, int dependeDe) {
        String fecha = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        return "backup_" + id + "_auto_inc_" + dependeDe + "_" + fecha;
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
