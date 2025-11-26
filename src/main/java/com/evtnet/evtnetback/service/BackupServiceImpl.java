package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ProgramacionBackup;
import com.evtnet.evtnetback.dto.backup.DTOBackup;
import com.evtnet.evtnetback.dto.backup.DTOProgramacionBackupsAutomaticos;
import com.evtnet.evtnetback.repository.ProgramacionBackupRepository;
import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BackupServiceImpl extends BaseServiceImpl <ProgramacionBackup, Long> implements BackupService {
    @Value("${app.backup.directory}")
    private String backupDirectory;

    private final ProgramacionBackupRepository programacionBackupRepository;
    private final RegistroSingleton registroSingleton;
    private final ParametroSistemaService parametroSistemaService;

    public BackupServiceImpl(ProgramacionBackupRepository programacionBackupRepository,
                             RegistroSingleton registroSingleton,
                             ParametroSistemaService parametroSistemaService) {
        super(programacionBackupRepository);
        this.programacionBackupRepository = programacionBackupRepository;
        this.registroSingleton = registroSingleton;
        this.parametroSistemaService = parametroSistemaService;
    }

    @Override
    public Page<DTOBackup> listarBackups(int page) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);

        Path basePath = Paths.get(backupDirectory);
        if (!Files.exists(basePath)) {
            return Page.empty();
        }

        List<DTOBackup> backups = new ArrayList<>();

        try (Stream<Path> stream = Files.list(basePath)) {

            List<Path> directorios = stream
                    .filter(Files::isDirectory)
                    .toList();

            for (Path p : directorios) {

                String nombre = p.getFileName().toString();
                String[] partes = nombre.split("_");

                int id = Integer.parseInt(partes[1]);
                String programacion = nombre.contains("manual") ? "Manual" : "Automática";
                boolean esPendiente = nombre.contains("pendiente");
                Boolean pendiente = programacion.equals("Manual") ? esPendiente : null;

                String tipo;
                if (nombre.contains("auto_inc")) tipo = "Incremental";
                else tipo = "Completa";

                Integer dependeDe = null;
                if (nombre.contains("auto_inc")) {
                    dependeDe = Integer.parseInt(partes[5]);
                }

                String fecha = partes[partes.length - 2];
                String hora = partes[partes.length - 1];
                LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora.replace("-", ":"));

                Double tamano = null;
                if (!esPendiente) {
                    long bytes = calcularTamanoBackup(p);
                    double gb = bytes / 1024d / 1024d / 1024d;
                    tamano = Math.round(gb * 10.0) / 10.0;
                }
                backups.add(DTOBackup.builder()
                                .id(id)
                                .ruta(nombre)
                                .tamano(tamano)
                                .fechaHora(fechaHora.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .tipo(tipo)
                                .programacion(programacion)
                                .pendiente(pendiente)
                                .dependeDe(dependeDe)
                        .build());
            }
        }

        backups.sort(Comparator.comparing(DTOBackup::getFechaHora).reversed());

        int from = page * longitudPagina;
        int to = Math.min(from + longitudPagina, backups.size());
        List<DTOBackup> pagina = from < backups.size() ? backups.subList(from, to) : List.of();

        Pageable pageable = PageRequest.of(page, longitudPagina);

        return new PageImpl<>(pagina, pageable, backups.size());
    }

    @Override
    public void crearBackupManual(Long fechaHora) throws Exception {

        LocalDateTime fechaProgramada =
                Instant.ofEpochMilli(fechaHora)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

        if (fechaProgramada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha debe ser futura");
        }

        int siguienteNumero = obtenerSiguienteNumero();

        String nombreCarpeta = String.format(
                "backup_%d_manual_pendiente_%s",
                siguienteNumero,
                fechaProgramada.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        );

        Path ruta = Paths.get(backupDirectory, nombreCarpeta);

        Files.createDirectories(ruta);
        registroSingleton.write("Parametros", "backup_manual", "creacion", "Backup manual de ruta " + nombreCarpeta);

    }

    @Override
    public void eliminarBackup(DTOBackup dto) throws Exception {

        Path base = Paths.get(backupDirectory);
        if (!Files.exists(base)) return;

        List<Path> todas = obtenerTodasLasCarpetas(base);

        Path carpetaObjetivo = Paths.get(backupDirectory, dto.getRuta());

        List<Path> aEliminar = new ArrayList<>();
        aEliminar.add(carpetaObjetivo);

        if (dto.getProgramacion().equals("Automática")) {

            if (dto.getTipo().equals("Completa")) {

                int idBase = dto.getId();

                for (Path p : todas) {
                    String nombre = p.getFileName().toString();
                    if (nombre.contains("auto_inc")) {
                        String[] partes = nombre.split("_");
                        int dependeDe = Integer.parseInt(partes[5]);
                        if (dependeDe == idBase) aEliminar.add(p);
                    }
                }
            }

            if (dto.getTipo().equals("Incremental")) {

                int idInc = dto.getId();

                for (Path p : todas) {
                    String nombre = p.getFileName().toString();
                    if (nombre.contains("auto_inc")) {
                        String[] partes = nombre.split("_");
                        int dependeDe = Integer.parseInt(partes[5]);
                        if (dependeDe == idInc) aEliminar.add(p);
                    }
                }
            }
        }

        for (Path p : aEliminar) {
            borrarDirectorio(p);
            registroSingleton.write("Parametros", "backup", "eliminacion", "Backup de ruta " + p);
        }
    }

    @Override
    public void programarAutomatica(DTOProgramacionBackupsAutomaticos dto) throws Exception {

        Integer meses = dto.getFrecuencia().getMeses();
        Integer dias = dto.getFrecuencia().getDias();
        Integer horas = dto.getFrecuencia().getHoras();

        if ((meses == 0 || meses == null) &&
                (dias == 0 || dias == null) &&
                (horas == 0 || horas == null)) {
            throw new IllegalArgumentException("Debe establecer al menos un valor en meses, días u horas");
        }

        if (dias != null && dias > 30) {
            throw new IllegalArgumentException("No puede indicar más de 30 días");
        }

        if (horas != null && horas > 23) {
            throw new IllegalArgumentException("No puede indicar más de 23 horas");
        }

        if (dto.getFechaHoraInicio() == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }

        LocalDateTime fechaInicio =
                Instant.ofEpochMilli(dto.getFechaHoraInicio())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

        LocalDateTime ahora = LocalDateTime.now();

        programacionBackupRepository.findByActivaTrue().ifPresent(programacion -> {
            programacion.setFechaHoraBaja(ahora);
            programacion.setActiva(false);
            programacionBackupRepository.save(programacion);
        });

        ProgramacionBackup nueva = ProgramacionBackup.builder()
                        .fechaHoraAlta(ahora)
                        .fechaDesde(fechaInicio)
                        .meses(meses)
                        .dias(dias)
                        .horas(horas)
                        .copiasIncrementales(dto.getCopiasIncrementalesPorCompleta())
                        .copiasAConservar(dto.getCopiasAnterioresAConservar())
                        .activa(true)
                .build();

        nueva=programacionBackupRepository.save(nueva);

        registroSingleton.write("Parametros", "backup_automatico", "creacion", "Backup automatico de fecha " + fechaInicio);
    }

    @Override
    public ProgramacionBackup obtenerProgramacionActiva() {
        return programacionBackupRepository.findTopByActivaTrueOrderByFechaHoraAltaDesc();
    }

    @Override
    public DTOProgramacionBackupsAutomaticos obtenerProgramacionActivaDTO() throws Exception{
        ProgramacionBackup programacionBackup=programacionBackupRepository.findTopByActivaTrueOrderByFechaHoraAltaDesc();
        if(programacionBackup==null) throw new Exception("No hay copias programadas");
        return DTOProgramacionBackupsAutomaticos.builder()
                .fechaHoraInicio(programacionBackup.getFechaDesde()==null ? null
                        :programacionBackup.getFechaDesde().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .copiasAnterioresAConservar(programacionBackup.getCopiasAConservar())
                .copiasIncrementalesPorCompleta(programacionBackup.getCopiasIncrementales())
                .frecuencia(DTOProgramacionBackupsAutomaticos.Frecuencia.builder()
                        .dias(programacionBackup.getDias())
                        .meses(programacionBackup.getMeses())
                        .horas(programacionBackup.getHoras())
                        .build())
                .build();
    }

    //región métodos auxiliares
    private long calcularTamanoBackup(Path backupPath) throws IOException {
        if (!Files.exists(backupPath)) return 0;

        try (Stream<Path> walk = Files.walk(backupPath)) {
            return walk
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    })
                    .sum();
        }
    }

    private int obtenerSiguienteNumero() throws IOException {

        Path base = Paths.get(backupDirectory);

        if (!Files.exists(base)) {
            return 1;
        }

        try (Stream<Path> stream = Files.list(base)) {
            return stream
                    .filter(Files::isDirectory)
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.startsWith("backup_"))
                    .map(n -> Integer.parseInt(n.split("_")[1]))
                    .max(Comparator.naturalOrder())
                    .orElse(0) + 1;
        }
    }

    private List<Path> obtenerTodasLasCarpetas(Path base) throws IOException {

        try (Stream<Path> stream = Files.list(base)) {
            return stream
                    .filter(Files::isDirectory)
                    .toList();
        }
    }

    private void borrarDirectorio(Path path) throws IOException {

        if (!Files.exists(path)) return;

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
