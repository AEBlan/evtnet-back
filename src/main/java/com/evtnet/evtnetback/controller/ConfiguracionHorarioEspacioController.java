package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ConfiguracionHorarioEspacio;
import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Services.ConfiguracionHorarioEspacioService;
import com.evtnet.evtnetback.Services.ConfiguracionHorarioEspacioServiceImpl;
import com.evtnet.evtnetback.Services.EspacioServiceImpl;
import com.evtnet.evtnetback.dto.cronogramas.DTOCrearCronograma;
import com.evtnet.evtnetback.dto.cronogramas.DTOCrearExcepcion;
import com.evtnet.evtnetback.dto.cronogramas.DTOCrearHorario;
import com.evtnet.evtnetback.dto.cronogramas.DTOCronogramaEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOBusquedaEspacios;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cronogramas")
@AllArgsConstructor
public class ConfiguracionHorarioEspacioController extends BaseControllerImpl <ConfiguracionHorarioEspacio, ConfiguracionHorarioEspacioServiceImpl>  {
    private final ConfiguracionHorarioEspacioService configuracionHorarioEspacioService;

    @GetMapping("/obtenerCronogramasEspacio")
    public ResponseEntity obtenerCronogramasEspacio(@RequestParam(name="id", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerCronogramasEspacio(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los cronogramas del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerCronogramaEspacio")
    public ResponseEntity obtenerCronogramaEspacio(@RequestParam(name="idCronograma", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerCronogramaEspacio(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerDetalleCronograma")
    public ResponseEntity obtenerDetalleCronograma(@RequestParam(name="idCronograma", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerDetalleCronograma(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el detalle del cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/eliminarHorario")
    public ResponseEntity eliminarHorario(@RequestParam(name="idHorario", required=true) Long id){
        try{
            configuracionHorarioEspacioService.eliminarHorario(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo eliminar el horario - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerExcepcionesCronograma")
    public ResponseEntity obtenerExcepcionesCronograma(@RequestParam(name="idCronograma", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerExcepcionesCronograma(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener la excepciones del cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/eliminarExcepcion")
    public ResponseEntity eliminarExcepcion(@RequestParam(name="idExcepcion", required=true) Long id){
        try{
            configuracionHorarioEspacioService.eliminarExcepcionCronograma(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo eliminar la excepción - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/verificarVigencia")
    public ResponseEntity verificarVigencia(@RequestParam(name="idEspacio", required=true) Long idSubEspacio, @RequestParam(name="idCronograma", defaultValue = "0") Long idCronograma, @RequestParam(name="fechaDesde", required=true) Long fechaDesde, @RequestParam(name="fechaHasta", required=true) Long fechaHasta){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.verificarVigencia(idSubEspacio, idCronograma, fechaDesde, fechaHasta));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se verificar la vigencia del cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/crearCronograma")
    public ResponseEntity crearCronograma(@RequestBody DTOCrearCronograma dtoCrearCronograma){
        try{
            Long idCronograma=configuracionHorarioEspacioService.crearCronograma(dtoCrearCronograma);
            Map<String, Long> respuesta = Map.of("id", idCronograma);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear el cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerDatosCreacionHorario")
    public ResponseEntity obtenerDatosCreacionHorario(@RequestParam(name="idCronograma", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerDatosCreacionHorario(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los datos del cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/crearHorario")
    public ResponseEntity crearHorario(@RequestBody DTOCrearHorario dtoCrearHorario){
        try{
            configuracionHorarioEspacioService.crearHorario(dtoCrearHorario);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear el horario - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerDatosCreacionExcepcion")
    public ResponseEntity obtenerDatosCreacionExcepcion(@RequestParam(name="idCronograma", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerDatosCreacionExcepcion(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los datos del cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/crearExcepcion")
    public ResponseEntity crearExcepcion(@RequestBody DTOCrearExcepcion dtoCrearExcepcion){
        try{
            configuracionHorarioEspacioService.crearExcepcion(dtoCrearExcepcion);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear la excepción - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/buscarHorariosDisponibles")
    public ResponseEntity buscarHorariosDisponibles(@RequestParam(name="idEspacio", required=true) Long id, @RequestParam(name="dia", required=true) Long dia){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.buscarHorariosDisponibles(id, dia));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los horarios disponibles - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerPeriodosLibres")
    public ResponseEntity obtenerPeriodosLibres(@RequestParam(name="idEspacio", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(configuracionHorarioEspacioService.obtenerPeriodosLibres(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los períodos libres - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificarCronograma")
    public ResponseEntity modificarCronograma(@RequestBody DTOCronogramaEspacio dtoCronograma){
        try{
            configuracionHorarioEspacioService.modificarCronograma(dtoCronograma);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear el cronograma - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}
