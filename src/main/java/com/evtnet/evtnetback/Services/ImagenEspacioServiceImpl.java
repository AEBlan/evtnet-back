package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.Repositories.ImagenEspacioRepository;
import com.evtnet.evtnetback.Services.BaseServiceImpl;
import com.evtnet.evtnetback.Services.ImagenEspacioService;
import com.evtnet.evtnetback.Services.UploadsService; 
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImagenEspacioServiceImpl extends BaseServiceImpl<ImagenEspacio, Long>
        implements ImagenEspacioService {

    private final ImagenEspacioRepository imagenRepo;
    private final EspacioRepository espacioRepo;
    private final UploadsService uploads;

    public ImagenEspacioServiceImpl(ImagenEspacioRepository imagenRepo,
                                    EspacioRepository espacioRepo,
                                    UploadsService uploads) {
        super(imagenRepo);
        this.imagenRepo = imagenRepo;
        this.espacioRepo = espacioRepo;
        this.uploads = uploads;
    }

    @Override
    @Transactional
    public DTOImagenEspacio subirImagen(Long espacioId, MultipartFile file) {
        Espacio espacio = espacioRepo.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no existe"));

        String subpath = "espacios/" + espacioId;
        String url = uploads.savePngOrSvg(file, subpath);

        int orden = imagenRepo.findTopByEspacio_IdOrderByOrdenDesc(espacioId)
                .map(x -> x.getOrden() + 1)  // getOrden() existe aunque el campo sea snake_case? Sí, porque el campo es "orden"
                .orElse(0);

        ImagenEspacio e = ImagenEspacio.builder()
                .espacio(espacio)
                .imagen(url)
                .orden(orden)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        e = imagenRepo.save(e);

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
        return imagenRepo.findByEspacio_IdOrderByOrdenAsc(espacioId).stream()
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
        ImagenEspacio e = imagenRepo.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        uploads.deleteByPublicUrl(e.getImagen());
        imagenRepo.delete(e);

        // Recompactar orden:
        List<ImagenEspacio> restantes =
                imagenRepo.findByEspacio_IdOrderByOrdenAsc(e.getEspacio().getId());

        int i = 0;
        for (ImagenEspacio img : restantes) {
            if (img.getOrden() != i) {
                img.setOrden(i);
                imagenRepo.save(img);
            }
            i++;
        }
    }

    @Override
    @Transactional
    public DTOImagenEspacio hacerPortada(Long imagenId) {
        ImagenEspacio target = imagenRepo.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        Long espacioId = target.getEspacio().getId();

        List<ImagenEspacio> todas = imagenRepo.findByEspacio_IdOrderByOrdenAsc(espacioId);
        todas.removeIf(img -> img.getId().equals(imagenId));

        target.setOrden(0);
        imagenRepo.save(target);

        int orden = 1;
        for (ImagenEspacio img : todas) {
            img.setOrden(orden++);
            imagenRepo.save(img);
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
        ImagenEspacio e = imagenRepo.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));

        e.setOrden(nuevoOrden);
        e = imagenRepo.save(e);

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
}