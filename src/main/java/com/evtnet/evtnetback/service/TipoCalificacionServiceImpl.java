package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.TipoCalificacion;
import com.evtnet.evtnetback.repository.TipoCalificacionRepository;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacion;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacionSelect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoCalificacionServiceImpl extends BaseServiceImpl <TipoCalificacion, Long> implements TipoCalificacionService {

    @Value("${app.storage.iconos:/app/storage/iconos}")
    private String imagenesDirectorio;

    private final TipoCalificacionRepository tipoCalificacionRepository;

    public TipoCalificacionServiceImpl(TipoCalificacionRepository tipoCalificacionRepository) {
        super(tipoCalificacionRepository);
        this.tipoCalificacionRepository = tipoCalificacionRepository;
    }
    @Override
    public Page<DTOTipoCalificacion> obtenerListaTipoCalificacion(Pageable pageable) throws Exception {
        Specification<TipoCalificacion> spec = Specification.where(null);
        Page<TipoCalificacion> tiposCalificacion = tipoCalificacionRepository.findAll(spec, pageable);
        return tiposCalificacion
                .map(ic-> {
                    String base64Image = encodeFileToBase64(ic.getImagen());
                    String[] parts = base64Image.split(",");
                    String base64Data = parts[1];
                    String mimeType = parts[0].split(";")[0].split(":")[1];
                    String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                    return DTOTipoCalificacion.builder()
                            .id(ic.getId())
                            .nombre(ic.getNombre())
                            .url(base64Data)
                            .fechaBaja(ic.getFechaHoraBaja()==null ? null
                                    :ic.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .contentType(contentType)
                            .build();
                });
    }

    @Override
    public List<DTOTipoCalificacionSelect> obtenerTiposCalificacionSelect() throws Exception {
        List<TipoCalificacion> tiposCalificacion = tipoCalificacionRepository.findAll();
        return tiposCalificacion.stream().map(tc->DTOTipoCalificacionSelect.builder()
                    .id(tc.getId())
                    .nombre(tc.getNombre())
                    .build())
                .collect(Collectors.toList());
    }

    @Override
    public DTOTipoCalificacion obtenerTipoCalificacionCompleto(Long id) throws Exception {
        TipoCalificacion tipoCalificacion = tipoCalificacionRepository.findById(id).get();
//        TipoCalificacion tipoCalificacion = tipoCalificacionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Icono no encontrado"));
        String base64Image = encodeFileToBase64(tipoCalificacion.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
        return DTOTipoCalificacion.builder()
                .id(tipoCalificacion.getId())
                .nombre(tipoCalificacion.getNombre())
                .url(base64Data)
                .contentType(contentType)
                .build();
    }

    @Override
    public void altaTipoCalificacion(DTOTipoCalificacion tipoCalificacion) throws Exception {
        this.save(TipoCalificacion.builder()
                .imagen(guardarImagenBase64(tipoCalificacion.getUrl(), tipoCalificacion.getId()))
                .nombre(tipoCalificacion.getNombre())
                .build());
    }

    @Override
    public void modificarTipoCalificacion(DTOTipoCalificacion tipoCalificacion) throws Exception {
        tipoCalificacionRepository.update(tipoCalificacion.getId(), guardarImagenBase64(tipoCalificacion.getUrl(), tipoCalificacion.getId()), tipoCalificacion.getNombre());
    }

    @Override
    public void bajaTipoCalificacion(Long id) throws Exception {
        tipoCalificacionRepository.delete(id, LocalDateTime.now());
    }

    private String guardarImagenBase64(String dataUrl, Long id) throws IOException {

        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "icono" + id + extension;
        if (!Files.exists(Paths.get(imagenesDirectorio))) {
            Files.createDirectories(Paths.get(imagenesDirectorio));
        }
        Path filePath=Paths.get(imagenesDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return filePath.toString();
    }

    private String encodeFileToBase64(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            String contentType;
            if (filePath.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else {
                contentType = "image/png";
            }

            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:" + contentType + ";base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo archivo de imagen: " + filePath, e);
        }
    }
}
