package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.MedioDePago;
import com.evtnet.evtnetback.Repositories.MedioDePagoRepository;
import com.evtnet.evtnetback.dto.medioDePago.DTOMedioDePago;
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
@Service
public class MedioDePagoServiceImpl extends BaseServiceImpl <MedioDePago, Long> implements MedioDePagoService {

    private final MedioDePagoRepository medioDePagoRepository;
    @Value("${app.storage.iconos:/app/storage/iconos}")
    private String imagenesDirectorio;

    public MedioDePagoServiceImpl(MedioDePagoRepository medioDePagoRepository) {
        super(medioDePagoRepository);
        this.medioDePagoRepository = medioDePagoRepository;
    }

    @Override
    public Page<DTOMedioDePago> obtenerListaMedioDePago(Pageable pageable) throws Exception {
        Specification<MedioDePago> spec = Specification.where(null);
        Page<MedioDePago> tiposCalificacion = medioDePagoRepository.findAll(spec, pageable);
        return tiposCalificacion
                .map(ic-> {
                    String base64Data="";
                    String contentType=null;
                    if(ic.getIcono()!=null){
                        String base64Image = encodeFileToBase64(ic.getIcono());
                        String[] parts = base64Image.split(",");
                        base64Data = parts[1];
                        String mimeType = parts[0].split(";")[0].split(":")[1];
                        contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                    }
                    return DTOMedioDePago.builder()
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
    public DTOMedioDePago obtenerMedioDePagoCompleto(Long id) throws Exception {
        MedioDePago medioDePago = medioDePagoRepository.findById(id).get();
//        MedioDePago medioDePago = medioDePagoRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Icono no encontrado"));
        String base64Data="";
        String contentType=null;
        if(medioDePago.getIcono()!=null) {
            String base64Image = encodeFileToBase64(medioDePago.getIcono());
            String[] parts = base64Image.split(",");
            base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
        }
        return DTOMedioDePago.builder()
                .id(medioDePago.getId())
                .nombre(medioDePago.getNombre())
                .url(base64Data)
                .contentType(contentType)
                .build();
    }

    @Override
    public void altaMedioDePago(DTOMedioDePago medioDePago) throws Exception {
        this.save(MedioDePago.builder()
                .icono(guardarImagenBase64(medioDePago.getUrl(), medioDePago.getId()))
                .nombre(medioDePago.getNombre())
                .build());
    }

    @Override
    public void modificarMedioDePago(DTOMedioDePago medioDePago) throws Exception {
        medioDePagoRepository.update(medioDePago.getId(), guardarImagenBase64(medioDePago.getUrl(), medioDePago.getId()), medioDePago.getNombre());
    }

    @Override
    public void bajaMedioDePago(Long id) throws Exception {
        medioDePagoRepository.delete(id, LocalDateTime.now());
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
