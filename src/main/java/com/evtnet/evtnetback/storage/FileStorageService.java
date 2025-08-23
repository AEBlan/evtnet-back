package com.evtnet.evtnetback.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${app.storage.perfiles}") String basePath) throws IOException {
        this.baseDir = Paths.get(basePath).toAbsolutePath().normalize();
        Files.createDirectories(this.baseDir);
    }

    public String guardarPerfil(String username, byte[] contenido, String originalFilename) throws IOException {
        String ext = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String filename = username + ext;
        Path destino = baseDir.resolve(filename);
        Files.write(destino, contenido, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return filename; // guardarás esto en la DB (Usuario.fotoPerfil)
    }

    public byte[] leerPerfil(String filename) throws IOException {
        Path p = baseDir.resolve(filename);
        if (!Files.exists(p)) throw new NoSuchFileException("No existe " + filename);
        return Files.readAllBytes(p);
    }
}
