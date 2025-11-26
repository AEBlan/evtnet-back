package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mensaje.DTOMensajeResponse;
import com.evtnet.evtnetback.entity.Mensaje;
import java.util.List;


public interface MensajeService extends BaseService <Mensaje, Long> {
    Mensaje enviarMensaje(Long chatId, Long usuarioAutorId, String texto);
    List<DTOMensajeResponse> obtenerHistorial(Long chatId);

}
