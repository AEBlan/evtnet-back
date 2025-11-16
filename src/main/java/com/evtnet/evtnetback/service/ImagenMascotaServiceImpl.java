package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.DTOAltaImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascotaLista;
import com.evtnet.evtnetback.dto.mascota.DTOModificarImagenMascota;
import com.evtnet.evtnetback.entity.ImagenMascota;
import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.repository.ImagenMascotaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
public class ImagenMascotaServiceImpl extends BaseServiceImpl<ImagenMascota, Long>
        implements ImagenMascotaService {

    @Value("${app.storage.mascotas:/app/storage/mascotas}")
    private String mascotasDirectorio;

    private final ImagenMascotaRepository imagenMascotaRepository;

    private final ParametroSistemaService parametroSistemaService;

    public ImagenMascotaServiceImpl(ImagenMascotaRepository imagenMascotaRepository, ParametroSistemaService parametroSistemaService) {
        super(imagenMascotaRepository);
        this.imagenMascotaRepository = imagenMascotaRepository;
        this.parametroSistemaService = parametroSistemaService;
    }

    @Override
    public Page<DTOImagenMascota> obtenerListaImagenMascota(int page) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(page, longitudPagina, Sort.by("id").ascending());
        Specification<ImagenMascota> spec = Specification.where(null);
        Page<ImagenMascota> imagenesMascota = imagenMascotaRepository.findAll(spec, pageable);
        return imagenesMascota
                .map(im -> {
                    String base64Image = encodeFileToBase64(im.getImagen());
                    String[] parts = base64Image.split(",");
                    String base64Data = parts[1];
                    String mimeType = parts[0].split(";")[0].split(":")[1];
                    String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                    return DTOImagenMascota.builder()
                            .id(im.getId())
                            .nombre(im.getNombre())
                            .url(base64Data)
                            .fechaAlta(im.getFechaHoraAlta() == null ? null
                                    : im.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaBaja(im.getFechaHoraBaja() == null ? null
                                    : im.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .contentType(contentType)
                            .build();
                });
    }

    @Override
    public DTOImagenMascota obtenerImagenMascotaCompleta(Long id) throws Exception {
        ImagenMascota imagenMascota = imagenMascotaRepository.findById(id).get();
        String base64Image = encodeFileToBase64(imagenMascota.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
        return DTOImagenMascota.builder()
                .id(imagenMascota.getId())
                .nombre(imagenMascota.getNombre())
                .url(base64Data)
                .contentType(contentType)
                .build();
    }

    @Override
    public void altaImagenMascota(DTOAltaImagenMascota imagenMascota) throws Exception {
        ImagenMascota imagen = this.save(ImagenMascota.builder()
                .nombre(imagenMascota.getNombre())
                .imagen("")
                .fechaHoraAlta(LocalDateTime.now())
                .build());

        imagen.setImagen(guardarImagenBase64(imagenMascota.getUrl(), imagen.getId()));
        this.save(imagen);
    }

    @Override
    public void modificarImagenMascota(DTOModificarImagenMascota imagenMascota) throws Exception {
        ImagenMascota imagen = imagenMascotaRepository.findById(imagenMascota.getId()).get();
        imagen.setNombre(imagenMascota.getNombre());

        // Only save the image if it's a new base64 data URL
        if (imagenMascota.getUrl().startsWith("data:")) {
            imagen.setImagen(guardarImagenBase64(imagenMascota.getUrl(), imagenMascota.getId()));
        }

        this.save(imagen);
    }

    @Override
    public void bajaImagenMascota(Long id) throws Exception {
        ImagenMascota imagenMascota = imagenMascotaRepository.findById(id).orElseThrow(() -> new Exception("No se encontró la imagen"));

        List<InstanciaMascota> instancias = imagenMascota.getInstanciaMascotaSecuencias().stream().filter(s -> s.getFechaHoraBaja() == null).map(s -> s.getInstanciaMascota()).filter(i -> i.getFechaHoraBaja() == null).toList();

        boolean vinculada = !instancias.isEmpty();

        if (vinculada) {

            String ins = "";
            for (InstanciaMascota instancia : instancias) {
                ins += ", {\"nombre\":\"" + instancia.getNombre()+ "\", \"id\": " + instancia.getId() +"}";
            }
            ins = ins.substring(2);
            ins = "[" + ins + "]";
            throw new HttpErrorException(806, ins);
        }
        imagenMascotaRepository.delete(id, LocalDateTime.now());
    }

    @Override
    public ImagenMascota obtenerImagen(Long idImagen) throws Exception {
        return imagenMascotaRepository.findById(idImagen)
                .orElseThrow(() -> new Exception("No se encontró la imagen con ID " + idImagen));
    }

    @Override
    public List<DTOImagenMascota> obtenerListaImagen() throws Exception {
        List<ImagenMascota> imagenesMascota = imagenMascotaRepository.findAll();
        List<DTOImagenMascota> dtoImagenesMascota = new ArrayList<>();
        for (ImagenMascota imagen : imagenesMascota) {
            String base64Image = encodeFileToBase64(imagen.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            dtoImagenesMascota.add(DTOImagenMascota.builder()
                    .id(imagen.getId())
                    .nombre(imagen.getNombre())
                    .url(base64Data)
                    .contentType(contentType)
                    .build());
        }
        return dtoImagenesMascota;
    }

    private String guardarImagenBase64(String dataUrl, Long id) throws IOException {
        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "mascota" + id + extension;
        if (!Files.exists(Paths.get(mascotasDirectorio))) {
            Files.createDirectories(Paths.get(mascotasDirectorio));
        }
        Path filePath = Paths.get(mascotasDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return filePath.toString();
    }

    private String encodeFileToBase64(String fileName) {
        try {
            Path filePath = Paths.get(fileName).toAbsolutePath().normalize();
            byte[] fileContent = Files.readAllBytes(filePath);

            String contentType;
            if (filePath.toString().endsWith(".svg")) {
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

    @Override
    public List<DTOImagenMascotaLista> obtenerLista() throws Exception {
        List<ImagenMascota> imagenesMascota = imagenMascotaRepository.findAllActivas();
        List<DTOImagenMascotaLista> dtoLista = new ArrayList<>();

        for (ImagenMascota imagen : imagenesMascota) {
            String base64Image = encodeFileToBase64(imagen.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];

            dtoLista.add(DTOImagenMascotaLista.builder()
                    .id(imagen.getId())
                    .nombre(imagen.getNombre())
                    .url(base64Data)
                    .build());
        }
        return dtoLista;
    }
}