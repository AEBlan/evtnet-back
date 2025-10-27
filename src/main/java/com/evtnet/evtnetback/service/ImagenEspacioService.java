package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ImagenEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOActualizarImagenesEspacio;
import com.evtnet.evtnetback.dto.imagenes.DTOImagenEspacio; // <-- DTO
import com.evtnet.evtnetback.dto.imagenes.DTOObtenerImagenEspacio;
import org.springframework.web.multipart.MultipartFile;      // <-- Multipart
import java.util.List;                                      // <-- List

public interface ImagenEspacioService extends BaseService<ImagenEspacio, Long> {
    DTOImagenEspacio subirImagen(Long espacioId, MultipartFile file);
    List<DTOImagenEspacio> listar(Long espacioId);
    void eliminar(Long imagenId);
    DTOImagenEspacio hacerPortada(Long imagenId);
    DTOImagenEspacio cambiarOrden(Long imagenId, Integer nuevoOrden);

    DTOObtenerImagenEspacio obtenerImagen(Long idEspacio, int orden)throws Exception;
    void actualizarImagenes(DTOActualizarImagenesEspacio dtoImagenes)throws Exception;
}