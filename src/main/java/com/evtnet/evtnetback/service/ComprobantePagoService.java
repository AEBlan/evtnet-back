package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.comprobante.DTOComprobante;
import com.evtnet.evtnetback.dto.comprobante.DTOComprobanteSimple;

import java.util.List;

public interface ComprobantePagoService {

    DTOComprobante obtener(Long numero) throws Exception;

    byte[] obtenerArchivo(Long numero) throws Exception;

    List<DTOComprobanteSimple> obtenerMisComprobantes() throws Exception;
}
