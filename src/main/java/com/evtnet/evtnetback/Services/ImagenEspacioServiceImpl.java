package com.evtnet.evtnetback.Services.imagenes;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.Repositories.ImagenEspacioRepository;
import com.evtnet.evtnetback.Services.BaseServiceImpl;
import com.evtnet.evtnetback.Services.ImagenEspacioService;   // <-- import interfaz
import com.evtnet.evtnetback.services.UploadsService;        // <-- paquete lowercase
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
                .map(x -> x.getOrden() + 1)   // ahora Optional tipado
                .orElse(0);

        // sin builder para no depender de Lombok aquí
        ImagenEspacio e = new ImagenEspacio();
        e.setEspacio(espacio);
        e.setImagen(url);
        e.setOrden(orden);
        e.setFecha_hora_alta(LocalDateTime.now());
        e = imagenRepo.save(e);

        return new DTOImagenEspacio(
        e.getId(),
        espacioId,
        e.getImagen(),
        e.getOrden(),
        e.getFecha_hora_alta().toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<DTOImagenEspacio> listar(Long espacioId) {
        return imagenRepo.findByEspacio_IdOrderByOrdenAsc(espacioId).stream()
                .map(e -> new DTOImagenEspacio(
                        e.getId(),
                        espacioId,
                        e.getImagen(),
                        e.getOrden(),
                        e.getFecha_hora_alta().toString()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Long imagenId) {
        ImagenEspacio e = imagenRepo.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));
        uploads.deleteByPublicUrl(e.getImagen());
        imagenRepo.delete(e);

        // recompactar (opcional):
        List<ImagenEspacio> restantes = imagenRepo.findByEspacio_IdOrderByOrdenAsc(e.getEspacio().getId());
        int i = 0;
        for (ImagenEspacio img : restantes) {
            img.setOrden(i++);
            imagenRepo.save(img);
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
        return new DTOImagenEspacio(
        target.getId(),
        espacioId,
        target.getImagen(),
        target.getOrden(),
        target.getFecha_hora_alta().toString()
        );
    }

    @Override
    @Transactional
    public DTOImagenEspacio cambiarOrden(Long imagenId, Integer nuevoOrden) {
        ImagenEspacio e = imagenRepo.findById(imagenId)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no existe"));
        e.setOrden(nuevoOrden);
        e = imagenRepo.save(e);
        return new DTOImagenEspacio(
                e.getId(),
                e.getEspacio().getId(),
                e.getImagen(),
                e.getOrden(),
                e.getFecha_hora_alta().toString()
        );
    }

    @Override
    public boolean delete(Long id) throws Exception {
        ImagenEspacio e = this.findById(id);
        uploads.deleteByPublicUrl(e.getImagen());
        return super.delete(id);
    }
}
