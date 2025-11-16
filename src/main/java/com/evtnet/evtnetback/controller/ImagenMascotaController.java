package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.mascota.DTOAltaImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascotaLista;
import com.evtnet.evtnetback.dto.mascota.DTOModificarImagenMascota;
import com.evtnet.evtnetback.entity.ImagenMascota;
import com.evtnet.evtnetback.service.ImagenMascotaService;
import com.evtnet.evtnetback.service.ImagenMascotaServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/imagenesMascota")
@AllArgsConstructor
public class ImagenMascotaController extends BaseControllerImpl<ImagenMascota, ImagenMascotaServiceImpl> {

    private final ImagenMascotaService imagenMascotaService;

    @GetMapping("/obtenerImagenesMascota")
    public ResponseEntity<Page<DTOImagenMascota>> obtenerListaImagenesMascota(@RequestParam(name = "page", defaultValue = "0") int page) throws Exception {
        return ResponseEntity.ok(imagenMascotaService.obtenerListaImagenMascota(page));
    }

    @GetMapping("/obtenerImagenMascotaCompleta")
    public ResponseEntity<DTOImagenMascota> obtenerImagenMascotaCompleta(@RequestParam(name = "id", required = true) Long id) throws Exception {
        return ResponseEntity.ok(imagenMascotaService.obtenerImagenMascotaCompleta(id));
    }

    @PostMapping("/alta")
    public ResponseEntity<Void> altaImagenMascota(@RequestBody DTOAltaImagenMascota imagenMascota) throws Exception {
        imagenMascotaService.altaImagenMascota(imagenMascota);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modificar")
    public ResponseEntity<Void> modificarImagenMascota(@RequestBody DTOModificarImagenMascota imagenMascota) throws Exception {
        imagenMascotaService.modificarImagenMascota(imagenMascota);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/baja")
    public ResponseEntity<Void> bajaImagenMascota(@RequestParam(name = "id", required = true) Long id) throws Exception {
        imagenMascotaService.bajaImagenMascota(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obtener")
    public ResponseEntity<byte[]> obtenerImagen(@RequestParam(name = "idImagen", required = true) Long idImagen) throws Exception {
        try {
            ImagenMascota imagen = imagenMascotaService.obtenerImagen(idImagen);
            Path path = Paths.get(imagen.getImagen());
            byte[] imagenBytes = Files.readAllBytes(path);
            String mimeType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(imagenBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/obtenerListaImagenes")
    public ResponseEntity<List<DTOImagenMascota>> obtenerListaImagenes() throws Exception {
        return ResponseEntity.ok(imagenMascotaService.obtenerListaImagen());
    }

    @GetMapping("/obtenerLista")
    public ResponseEntity<List<DTOImagenMascotaLista>> obtenerLista() throws Exception {
        return ResponseEntity.ok(imagenMascotaService.obtenerLista());
    }
}