package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.DTOAltaImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascota;
import com.evtnet.evtnetback.dto.mascota.DTOImagenMascotaLista;
import com.evtnet.evtnetback.dto.mascota.DTOModificarImagenMascota;
import com.evtnet.evtnetback.entity.ImagenMascota;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ImagenMascotaService extends BaseService<ImagenMascota, Long> {
    Page<DTOImagenMascota> obtenerListaImagenMascota(int page) throws Exception;
    DTOImagenMascota obtenerImagenMascotaCompleta(Long id) throws Exception;
    void altaImagenMascota(DTOAltaImagenMascota imagenMascota) throws Exception;
    void modificarImagenMascota(DTOModificarImagenMascota imagenMascota) throws Exception;
    void bajaImagenMascota(Long id) throws Exception;
    ImagenMascota obtenerImagen(Long idImagen) throws Exception;
    List<DTOImagenMascota> obtenerListaImagen() throws Exception;
    List<DTOImagenMascotaLista> obtenerLista() throws Exception;
}