package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Espacio;
import com.evtnet.evtnetback.entity.ImagenEspacio;
import com.evtnet.evtnetback.repository.EspacioRepository;
import com.evtnet.evtnetback.repository.ImagenEspacioRepository;
import com.evtnet.evtnetback.dto.imagenes.DTOActualizarImagenesEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOObtenerImagenEspacio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImagenEspacioServiceImpl extends BaseServiceImpl<ImagenEspacio, Long>implements ImagenEspacioService {

    @Value("${app.storage.iconos:/app/storage/iconos}")
    private String iconosDirectorio;

    private final ImagenEspacioRepository imagenEspacioRepository;
    private final EspacioRepository espacioRepository;
    private final UploadsService uploads;

    public ImagenEspacioServiceImpl(ImagenEspacioRepository imagenEspacioRepository,
                                    EspacioRepository espacioRepository,
                                    UploadsService uploads) {
        super(imagenEspacioRepository);
        this.imagenEspacioRepository = imagenEspacioRepository;
        this.espacioRepository = espacioRepository;
        this.uploads = uploads;
    }

    @Override
    @Transactional
    public DTOImagenEspacio subirImagen(Long espacioId, MultipartFile file) {
        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no existe"));

        String subpath = "espacios/" + espacioId;
        String url = uploads.savePngOrSvg(file, subpath);

        int orden = imagenEspacioRepository.findTopByEspacio_IdOrderByOrdenDesc(espacioId)
                .map(x -> x.getOrden() + 1)  // getOrden() existe aunque el campo sea snake_case? Sí, porque el campo es "orden"
                .orElse(0);

        ImagenEspacio e = ImagenEspacio.builder()
                .espacio(espacio)
                .imagen(url)
                .orden(orden)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        e = imagenEspacioRepository.save(e);

        return DTOImagenEspacio.builder()
                .id(e.getId())
                .espacioId(espacioId)
                .imagen(e.getImagen())
                .orden(e.getOrden())
                .fechaHoraAlta(e.getFechaHoraAlta().toString())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DTOImagenEspacio> listar(Long espacioId) {
        return imagenEspacioRepository.findByEspacio_IdOrderByOrdenAsc(espacioId).stream()
                .map(e -> DTOImagenEspacio.builder()
                        .id(e.getId())
                        .espacioId(espacioId)
                        .imagen(e.getImagen())
                        .orden(e.getOrden())
                        .fechaHoraAlta(e.getFechaHoraAlta().toString())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Long imagenId) {
        ImagenEspacio e = imagenEspacioRepository.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        uploads.deleteByPublicUrl(e.getImagen());
        imagenEspacioRepository.delete(e);

        // Recompactar orden:
        List<ImagenEspacio> restantes =
                imagenEspacioRepository.findByEspacio_IdOrderByOrdenAsc(e.getEspacio().getId());

        int i = 0;
        for (ImagenEspacio img : restantes) {
            if (img.getOrden() != i) {
                img.setOrden(i);
                imagenEspacioRepository.save(img);
            }
            i++;
        }
    }

    @Override
    @Transactional
    public DTOImagenEspacio hacerPortada(Long imagenId) {
        ImagenEspacio target = imagenEspacioRepository.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        Long espacioId = target.getEspacio().getId();

        List<ImagenEspacio> todas = imagenEspacioRepository.findByEspacio_IdOrderByOrdenAsc(espacioId);
        todas.removeIf(img -> img.getId().equals(imagenId));

        target.setOrden(0);
        imagenEspacioRepository.save(target);

        int orden = 1;
        for (ImagenEspacio img : todas) {
            img.setOrden(orden++);
            imagenEspacioRepository.save(img);
        }

        return DTOImagenEspacio.builder()
                .id(target.getId())
                .espacioId(espacioId)
                .imagen(target.getImagen())
                .orden(target.getOrden())
                .fechaHoraAlta(target.getFechaHoraAlta().toString())
                .build();
    }

    @Override
    @Transactional
    public DTOImagenEspacio cambiarOrden(Long imagenId, Integer nuevoOrden) {
        ImagenEspacio e = imagenEspacioRepository.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        e.setOrden(nuevoOrden);
        e = imagenEspacioRepository.save(e);

        return DTOImagenEspacio.builder()
                .id(e.getId())
                .espacioId(e.getEspacio().getId())
                .imagen(e.getImagen())
                .orden(e.getOrden())
                .fechaHoraAlta(e.getFechaHoraAlta().toString())
                .build();
    }

    @Override
    public boolean delete(Long id) throws Exception {
        ImagenEspacio e = this.findById(id);
        uploads.deleteByPublicUrl(e.getImagen());
        return super.delete(id);
    }

    @Override
    public DTOObtenerImagenEspacio obtenerImagen(Long idEspacio, int orden)throws Exception{
        ImagenEspacio imagenEspacio=this.imagenEspacioRepository.findByEspacioYOrden(idEspacio, orden);
        String base64Image = encodeFileToBase64(imagenEspacio.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
        return DTOObtenerImagenEspacio.builder()
                .id(imagenEspacio.getId())
                .contentType(contentType)
                .content(base64Data)
                .build();
    }
    @Override
    public void actualizarImagenes(DTOActualizarImagenesEspacio dtoImagenes) throws Exception {
        Espacio espacio = this.espacioRepository.findById(dtoImagenes.getIdEspacio())
                .orElseThrow(() -> new Exception("No se encontró el espacio con ID " + dtoImagenes.getIdEspacio()));

        List<ImagenEspacio> imagenesBD = this.imagenEspacioRepository.findByEspacio_IdOrderByOrdenAsc(dtoImagenes.getIdEspacio());

        Set<Long> idsFront = dtoImagenes.getImagenes().stream()
                .filter(img -> img.getId() != null)
                .map(DTOActualizarImagenesEspacio.Imagen::getId)
                .collect(Collectors.toSet());

        List<ImagenEspacio> aEliminar = imagenesBD.stream()
                .filter(imgBD -> !idsFront.contains(imgBD.getId()))
                .collect(Collectors.toList());

        for (ImagenEspacio img : aEliminar) {
            img.setFechaHoraBaja(LocalDateTime.now());
            this.imagenEspacioRepository.save(img);
        }

        for (DTOActualizarImagenesEspacio.Imagen imagen : dtoImagenes.getImagenes()) {
            if (imagen.getId() == null || imagen.getId()==0) {
                ImagenEspacio nueva = ImagenEspacio.builder()
                        .espacio(espacio)
                        .fechaHoraAlta(LocalDateTime.now())
                        .orden(imagen.getOrden())
                        .imagen(guardarImagenBase64(imagen.getBlobUrl(), espacio.getId(),espacio.getNombre()))
                        .build();
                this.imagenEspacioRepository.save(nueva);
            } else {
                ImagenEspacio existente = this.imagenEspacioRepository.findById(imagen.getId())
                        .orElseThrow(() -> new Exception("No se encontró la imagen con ID " + imagen.getId()));
                existente.setOrden(imagen.getOrden());
                this.imagenEspacioRepository.save(existente);
            }
        }
    }

    private String guardarImagenBase64(String dataUrl, Long id, String nombreEspacio) throws IOException {

        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "imagenEspacio_" + nombreEspacio + id + extension;
        if (!Files.exists(Paths.get(iconosDirectorio))) {
            Files.createDirectories(Paths.get(iconosDirectorio));
        }
        Path filePath=Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return fileName;
    }

    private String encodeFileToBase64(String fileName) {
        try {
            Path filePath = Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
            byte[] fileContent = Files.readAllBytes(filePath);

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                if (fileName.toLowerCase().endsWith(".svg")) {
                    contentType = "image/svg+xml";
                } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
                    contentType = "image/jpeg";
                } else if (fileName.toLowerCase().endsWith(".gif")) {
                    contentType = "image/gif";
                } else {
                    contentType = "image/png";
                }
            }

            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:" + contentType + ";base64," + base64;

        } catch (IOException e) {
            throw new RuntimeException("Error leyendo archivo de imagen: " + fileName, e);
        }
    }

}