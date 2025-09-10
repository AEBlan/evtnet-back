package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.Services.ImagenEspacioServiceImpl;
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/imagenes-espacio")
public class ImagenEspacioController extends BaseControllerImpl<ImagenEspacio, ImagenEspacioServiceImpl> {

    public ImagenEspacioController(ImagenEspacioServiceImpl servicio) {
        this.servicio = servicio;
    }

    // SUBIR (PNG/SVG) – sin categoría
    @PostMapping(value="/espacios/{espacioId}/upload", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public DTOImagenEspacio upload(@PathVariable Long espacioId,
                                   @RequestParam("file") MultipartFile file) {
        return servicio.subirImagen(espacioId, file);
    }

    // LISTAR por espacio
    @GetMapping("/espacios/{espacioId}")
    public List<DTOImagenEspacio> listar(@PathVariable Long espacioId) {
        return servicio.listar(espacioId);
    }

    // PORTADA
    @PutMapping("/{imagenId}/portada")
    public DTOImagenEspacio portada(@PathVariable Long imagenId) {
        return servicio.hacerPortada(imagenId);
    }

    // CAMBIAR ORDEN
    @PutMapping("/{imagenId}/orden")
    public DTOImagenEspacio cambiarOrden(@PathVariable Long imagenId, @RequestParam Integer pos) {
        return servicio.cambiarOrden(imagenId, pos);
    }
}
