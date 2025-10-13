package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.Services.ImagenEspacioService;
import com.evtnet.evtnetback.Services.ImagenEspacioServiceImpl;
import com.evtnet.evtnetback.dto.cronogramas.DTOCrearExcepcion;
import com.evtnet.evtnetback.dto.imagenes.DTOActualizarImagenesEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/imagenesEspacios")
@AllArgsConstructor
public class ImagenEspacioController extends BaseControllerImpl<ImagenEspacio, ImagenEspacioServiceImpl> {

    private final ImagenEspacioService imagenEspacioService;

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

    @GetMapping("/obtener")
    public ResponseEntity obtenerImagen(@RequestParam(name="idEspacio", required=true) Long id, @RequestParam(name="orden", required=true) int orden){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(imagenEspacioService.obtenerImagen(id, orden));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la imagen - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/actualizar")
    public ResponseEntity actualizarImagenes(@RequestBody DTOActualizarImagenesEspacio dtoActualizarImagenesEspacio){
        try{
            imagenEspacioService.actualizarImagenes(dtoActualizarImagenesEspacio);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron actualizar las imágenes - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}
