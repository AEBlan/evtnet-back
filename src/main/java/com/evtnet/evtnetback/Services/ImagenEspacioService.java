package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio; // <-- DTO
import org.springframework.web.multipart.MultipartFile;      // <-- Multipart
import java.util.List;                                      // <-- List

public interface ImagenEspacioService extends BaseService<ImagenEspacio, Long> {
    DTOImagenEspacio subirImagen(Long espacioId, MultipartFile file);
    List<DTOImagenEspacio> listar(Long espacioId);
    void eliminar(Long imagenId);
    DTOImagenEspacio hacerPortada(Long imagenId);
    DTOImagenEspacio cambiarOrden(Long imagenId, Integer nuevoOrden);
}