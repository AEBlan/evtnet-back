package com.evtnet.evtnetback.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.evtnet.evtnetback.Entities.Registro;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.RegistroRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.dto.registros.DTORegistro;

import java.nio.file.*;

@Component
public class RegistroSingleton {
    
    private final RegistroRepository repository;    
    private final UsuarioRepository usuarioRepository;    

    private RequestUtils requestUtils;

    private final Path baseDir;

    public RegistroSingleton(@Value("${app.storage.logs}") String basePath, RegistroRepository repository, RequestUtils requestUtils, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.requestUtils = requestUtils;
        this.baseDir = Paths.get(basePath).toAbsolutePath().normalize();
        this.usuarioRepository = usuarioRepository;
    }

    public void write(String registro, String tipo, String subtipo, String descripcion) throws Exception {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        
        write(registro, tipo, subtipo, descripcion, username);
    }

    public void write(String registro, String tipo, String subtipo, String descripcion, String username) throws Exception {
        Optional<Registro> optRegistro = repository.findByNombre(registro);

        if(!optRegistro.isPresent()) {
            throw new Exception("No se encontró el registro");
        }

        Registro reg = optRegistro.get();

        if (!reg.getTipos().stream().map(t -> t.getNombre()).toList().contains(tipo)) {
            throw new Exception("El tipo no corresponde al registro");
        }

        if (!reg.getSubtipos().stream().map(t -> t.getNombre()).toList().contains(subtipo)) {
            throw new Exception("El subtipo no corresponde al registro");
        }

        

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String filename = baseDir.resolve(registro).resolve(LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue()) + "-" + String.format("%02d", LocalDate.now().getDayOfMonth()) + ".csv").toAbsolutePath().toString();
        String[] headers = {"Tipo", "Subtipo", "Fecha y Hora", "Usuario", "Solicitud HTTP", "Descripción"};
        String[] data = {
            tipo,
            subtipo,
            LocalDateTime.now().format(formatter),
            username,
            requestUtils.getFullRequestUrl(),
            descripcion
        };

        writeToCSV(filename, headers, data);
    }



    private void writeToCSV(String filename, String[] headers, String[] data) throws IOException {
        File file = new File(filename);

        file.getParentFile().mkdirs();

        boolean fileExists = file.exists();
        
        try (FileWriter writer = new FileWriter(file, true)) {
            // If file doesn't exist or is empty, write headers first
            if (!fileExists || file.length() == 0) {
                writer.write(String.join(",", (Arrays.asList(headers)).stream().map(h -> "\"" + h + "\"").toList()) + "\n");
            }
            
            // Write the data row
            writer.write(String.join(",", (Arrays.asList(data)).stream().map(h -> "\"" + h + "\"").toList()) + "\n");
        }
         
    }



    public RegistroReader getReader(String registro, LocalDateTime fechaDesde, LocalDateTime fechaHasta) throws Exception {
        return new RegistroReader(registro, fechaDesde, fechaHasta);
    }


    public class RegistroReader {
        private List<String> r_tipos = new ArrayList<>();
        private List<String> r_subtipos = new ArrayList<>();
        private List<String> r_usuarios = new ArrayList<>();

        List<DTORegistro> data = new ArrayList<>();

        public RegistroReader(String registro, LocalDateTime fechaDesde, LocalDateTime fechaHasta) throws Exception {
            Optional<Registro> optReg = repository.findByNombre(registro);

            if (!optReg.isPresent()) {
                throw new Exception("Registro no encontrado");
            }

            Path dir = baseDir.resolve(registro);
            
            Files.createDirectories(dir);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDate dateFrom = fechaDesde.toLocalDate();
            LocalDate dateTo = fechaHasta.toLocalDate();

            List<Path> sortedFiles = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.csv")) {
                for (Path filePath : stream) {
                    String fileName = filePath.getFileName().toString();
                    String dateStr = fileName.substring(0, fileName.lastIndexOf('.'));
                    
                    try {
                        LocalDate fileDate = LocalDate.parse(dateStr, formatter);
                        if (!fileDate.isBefore(dateFrom) && !fileDate.isAfter(dateTo)) {
                            sortedFiles.add(filePath);
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        
            sortedFiles.sort((a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()));


            for (Path filePath : sortedFiles) {
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    String line;
                    boolean firstLine = true;
                    while ((line = reader.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }
                        
                        String[] row = line.split(",");
                        for (int i = 0; i < row.length; i++) {
                            row[i] = row[i].trim();
                            row[i] = row[i].substring(1, row[i].length() - 1).trim();
                        }

                        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(row[3]);
                        String nombre = "";
                        String apellido = "";

                        if (optUsuario.isPresent()) {
                            nombre = optUsuario.get().getNombre();
                            apellido = optUsuario.get().getApellido();
                        }

                        LocalDateTime tmpDateTime = LocalDateTime.parse(row[2], formatterFull);

                        DTORegistro reg = DTORegistro.builder()
                            .tipo(row[0])
                            .subtipo(row[1])
                            .fechaHora(tmpDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .usuario(DTORegistro.Usuario.builder()
                                .username(row[3])
                                .nombre(nombre)
                                .apellido(apellido)
                                .build())
                            .solicitud(row[4])
                            .descripcion(row[5])
                            .build();

                        if (tmpDateTime.isAfter(fechaDesde) && tmpDateTime.isBefore(fechaHasta)) {
                            data.add(reg);
                        }
                    }
                }
            }
        }

        public RegistroReader tipos(String[] valores) {
            r_tipos = Arrays.asList(valores);
            return this;
        }

        public RegistroReader subtipos(String[] valores) {
            r_subtipos = Arrays.asList(valores);
            return this;
        }

        public RegistroReader usuarios(String[] valores) {
            r_usuarios = Arrays.asList(valores);
            return this;
        }

        public List<DTORegistro> read() {
            return data.stream().filter(r -> {
                if (!r_tipos.contains(r.getTipo()) && r_tipos.size() > 0) return false;
                if (!r_subtipos.contains(r.getSubtipo()) && r_subtipos.size() > 0) return false;
                if (!r_usuarios.contains(r.getUsuario().getUsername()) && r_usuarios.size() > 0) return false;
                return true;
            }).toList();
        }
    }
}
