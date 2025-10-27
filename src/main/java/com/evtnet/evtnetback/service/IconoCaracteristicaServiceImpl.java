package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Caracteristica;
import com.evtnet.evtnetback.entity.IconoCaracteristica;
import com.evtnet.evtnetback.repository.IconoCaracteristicaRepository;
import com.evtnet.evtnetback.repository.CaracteristicaRepository;
import com.evtnet.evtnetback.dto.espacios.DTOCaracteristicaSubEspacio;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class IconoCaracteristicaServiceImpl extends BaseServiceImpl<IconoCaracteristica, Long>
        implements IconoCaracteristicaService {

    @Value("${app.storage.iconos:/app/storage/iconos}")
    private String iconosDirectorio;

    private final IconoCaracteristicaRepository iconoCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final UploadsService uploads;

    public IconoCaracteristicaServiceImpl(IconoCaracteristicaRepository iconoCaracteristicaRepository,
                                          CaracteristicaRepository caracteristicaRepository,
                                          UploadsService uploads) {
        super(iconoCaracteristicaRepository);
        this.iconoCaracteristicaRepository = iconoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.uploads = uploads;
    }

//    @Override
//    @Transactional
//    public DTOIconoCaracteristica subirIcono(Long caracteristicaId, MultipartFile file) {
//        Caracteristica car = carRepo.findById(caracteristicaId)
//                .orElseThrow(() -> new IllegalArgumentException("Característica inexistente"));
//
//        String url = uploads.savePngOrSvg(file, "iconos");
//
//        // si preferís sin Lombok.builder() para evitar dependencia:
//        IconoCaracteristica icono = new IconoCaracteristica();
//        icono.setImagen(url);
//        icono.setFechaHoraAlta(LocalDateTime.now());
//        icono = iconoRepo.save(icono);
//
//        car.setIconoCaracteristica(icono);
//        carRepo.save(car);
//
//        return new DTOIconoCaracteristica(
//        icono.getId(),
//        icono.getImagen(),
//        icono.getFechaHoraAlta().toString()
//        );
//    }
//
//    @Override
//    @Transactional
//    public void eliminarIcono(Long iconoId) {
//        IconoCaracteristica ic = iconoRepo.findById(iconoId)
//                .orElseThrow(() -> new IllegalArgumentException("Ícono no existe"));
//
//        uploads.deleteByPublicUrl(ic.getImagen());
//        iconoRepo.delete(ic);
//    }

    @Override
    public Page<DTOIconoCaracteristica> obtenerListaIconoCaracteristica(Pageable pageable) throws Exception {
        Specification<IconoCaracteristica> spec = Specification.where(null);
        Page<IconoCaracteristica> iconosCaracteristica = iconoCaracteristicaRepository.findAll(spec, pageable);
        return iconosCaracteristica
                .map(ic-> {
                    String base64Image = encodeFileToBase64(ic.getImagen());
                    String[] parts = base64Image.split(",");
                    String base64Data = parts[1];
                    String mimeType = parts[0].split(";")[0].split(":")[1];
                    String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                    return DTOIconoCaracteristica.builder()
                            .id(ic.getId())
                            .url(base64Data)
                            .fechaAlta(ic.getFechaHoraAlta() == null ? null
                                    : ic.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaBaja(ic.getFechaHoraBaja()==null ? null
                                    :ic.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .contentType(contentType)
                            .build();
                });
    }

    @Override
    public DTOIconoCaracteristica obtenerIconoCaracteristicaCompleto(Long id) throws Exception {
        IconoCaracteristica iconoCaracteristica = iconoCaracteristicaRepository.findById(id).get();
//        IconoCaracteristica iconoCaracteristica = iconoCaracteristicaRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Icono no encontrado"));
        String base64Image = encodeFileToBase64(iconoCaracteristica.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
        return DTOIconoCaracteristica.builder()
                .id(iconoCaracteristica.getId())
                .url(base64Data)
                .contentType(contentType)
                .build();
    }

    @Override
    public void altaIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception {
        this.save(IconoCaracteristica.builder()
                .imagen(guardarImagenBase64(iconoCaracteristica.getUrl(), iconoCaracteristica.getId()))
                .fechaHoraAlta(LocalDateTime.now())
                .build());
    }

    @Override
    public void modificarIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception {
        iconoCaracteristicaRepository.update(iconoCaracteristica.getId(), guardarImagenBase64(iconoCaracteristica.getUrl(), iconoCaracteristica.getId()));
    }

    @Override
    public void bajaIconoCaracteristica(Long id) throws Exception {
        iconoCaracteristicaRepository.delete(id, LocalDateTime.now());
    }

    @Override
    public IconoCaracteristica obtenerIconosEspacio(Long idIcono)throws Exception {
        return iconoCaracteristicaRepository.findById(idIcono)
                .orElseThrow(() -> new Exception("No se encontró el ícono con ID " + idIcono));

    }

    @Override
    public List<DTOCaracteristicaSubEspacio> obtenerCaracteristicasSubEspacio(Long idEspacio) throws Exception{
        List<Caracteristica>caracteristicas=this.caracteristicaRepository.findBySubEspacio(idEspacio);
        List<DTOCaracteristicaSubEspacio> caracteristicaSubEspacios=new ArrayList<>();
        for(Caracteristica caracteristica:caracteristicas){
            String base64Image = encodeFileToBase64(caracteristica.getIconoCaracteristica().getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            caracteristicaSubEspacios.add(DTOCaracteristicaSubEspacio.builder()
                            .idEspacio(idEspacio)
                            .idIconoCaracteristica(caracteristica.getIconoCaracteristica().getId())
                            .nombre(caracteristica.getNombre())
                            .contentType(contentType)
                            .urlIcono(base64Data)
                    .build()
            );
        }
        return caracteristicaSubEspacios;
    }
    @Override
    public List<DTOIconoCaracteristica> obtenerListaIcono() throws Exception{
        List<IconoCaracteristica> iconosCaracteristica = iconoCaracteristicaRepository.findAll();
        List<DTOIconoCaracteristica> dtoIconoCaracteristicas = new ArrayList<>();
        for (IconoCaracteristica icono:iconosCaracteristica){
            String base64Image = encodeFileToBase64(icono.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            dtoIconoCaracteristicas.add(DTOIconoCaracteristica.builder()
                    .id(icono.getId())
                    .url(base64Data)
                    .contentType(contentType)
                    .build());
        }
        return dtoIconoCaracteristicas;
    }

    private String guardarImagenBase64(String dataUrl, Long id) throws IOException {

        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "caracteristica" + id + extension;
        if (!Files.exists(Paths.get(iconosDirectorio))) {
            Files.createDirectories(Paths.get(iconosDirectorio));
        }
        Path filePath=Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return filePath.toString();
    }

    private String encodeFileToBase64(String fileName) {
        try {
            Path filePath = Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
            byte[] fileContent = Files.readAllBytes(filePath);

            String contentType;
            if (filePath.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else {
                contentType = "image/png";
            }

            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:" + contentType + ";base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo archivo de imagen: " + fileName, e);
        }
    }
}
