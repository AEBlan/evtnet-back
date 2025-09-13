package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import com.evtnet.evtnetback.dto.iconos.DTOIconoCaracteristica;  // <-- import DTO
import org.springframework.web.multipart.MultipartFile;           // <-- import Multipart

public interface IconoCaracteristicaService extends BaseService<IconoCaracteristica, Long> {
    DTOIconoCaracteristica subirIcono(Long caracteristicaId, MultipartFile file);
    void eliminarIcono(Long iconoId);
}
