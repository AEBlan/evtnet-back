package com.evtnet.evtnetback.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadsService {

    @Value("${app.uploads.base-dir:uploads}")
    private String baseDir;

    public String savePngOrSvg(MultipartFile file, String subpath) {
        try {
            String original = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
            if (!original.matches(".*\\.(png|svg)$")) throw new IllegalArgumentException("Solo .png o .svg");
            String ct = Optional.ofNullable(file.getContentType()).orElse("");
            if (!ct.equals("image/png") && !ct.equals("image/svg+xml")) throw new IllegalArgumentException("Content-Type inválido");

            Path folder = Path.of(baseDir, subpath);
            Files.createDirectories(folder);

            String ext = original.substring(original.lastIndexOf('.')); // .png | .svg
            String filename = UUID.randomUUID() + ext;
            file.transferTo(folder.resolve(filename).toFile());

            subpath = subpath.replace("\\", "/");
            return "/uploads/" + (subpath.isEmpty() ? "" : subpath + "/") + filename;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo", e);
        }
    }
    public void deleteByPublicUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) return;
        try {
            // Si viene como /uploads/..., convertirlo a ruta física usando baseDir
            String prefix = "/uploads/";
            Path path;
            if (publicUrl.startsWith(prefix)) {
                String rel = publicUrl.substring(prefix.length());
                path = Paths.get(baseDir).resolve(rel).toAbsolutePath().normalize();
            } else {
                // Si te guardaron una absoluta en BD, intentar borrarla tal cual
                path = Paths.get(publicUrl).toAbsolutePath().normalize();
            }
            Files.deleteIfExists(path);
        } catch (Exception ex) {
            // loggear si querés, pero no reventar
        }
    }
}
