package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ConfiguracionHorarioEspacio;
import com.evtnet.evtnetback.dto.cronogramas.*;
import com.evtnet.evtnetback.dto.espacios.DTOVerificacionVigencia;

import java.util.List;

public interface ConfiguracionHorarioEspacioService extends BaseService<ConfiguracionHorarioEspacio, Long> {
    DTOCronogramasEspacio obtenerCronogramasEspacio(Long idSubEspacio)throws Exception;
    DTOCronogramaEspacio obtenerCronogramaEspacio(Long idCronograma)throws Exception;
    DTODetalleCronograma obtenerDetalleCronograma(Long idCronograma)throws Exception;
    void eliminarHorario(Long idHorario)throws Exception;
    DTOExcepcionesCronograma obtenerExcepcionesCronograma(Long idCronograma)throws Exception;
    void eliminarExcepcionCronograma(Long idExcepcion)throws Exception;
    DTOVerificacionVigencia verificarVigencia(Long idSubEspacio, Long idCronograma, Long fechaDesde, Long fechaHasta)throws Exception;
    Long crearCronograma(DTOCrearCronograma dtoCrearCronograma)throws Exception;
    void modificarCronograma(DTOCronogramaEspacio dtoCronograma)throws Exception;
    DTODatosCreacionHorario obtenerDatosCreacionHorario(Long idCronograma)throws Exception;
    void crearHorario(DTOCrearHorario dtoCrearHorario)throws Exception;
    DTODatosCreacionExcepcion obtenerDatosCreacionExcepcion(Long idCronograma)throws Exception;
    void crearExcepcion(DTOCrearExcepcion dtoCrearExcepcion)throws Exception;
    List<DTOHorarioDisponible>buscarHorariosDisponibles(Long idSubEspacio, Long dia)throws Exception;
    List<DTOPeriodoDisponible> obtenerPeriodosLibres(Long idSubEspacio)throws Exception;
}
