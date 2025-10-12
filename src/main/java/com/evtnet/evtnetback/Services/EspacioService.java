package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;

// imports para #US_ESP_1
import com.evtnet.evtnetback.dto.espacios.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface EspacioService extends BaseService<Espacio, Long> {
    Long crearEspacio(DTOCrearEspacio dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception;
    DTOEspacio obtenerEspacio(Long id, String username) throws Exception;
    DTOEspacioEditar obtenerEspacioEditar(Long id, String username) throws Exception;
    void editarEspacio(DTOEspacioEditar dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception;
    void dejarDeAdministrar(Long id, String username) throws Exception;
    String obtenerNombreEspacio(Long id)throws Exception;
    List<DTOTipoEspacio> obtenerTiposEspacio() throws Exception;
    List<DTOResultadoBusquedaEspacios> buscarEspacios(DTOBusquedaEspacios dtoEspacio) throws Exception;
    List<DTOResultadoBusquedaMisEspacios> buscarMisEspacios(DTOBusquedaMisEspacios dtoEspacio) throws Exception;
}
