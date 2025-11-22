package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.solicitudesEspacio.DTOBusquedaSEP;
import com.evtnet.evtnetback.dto.solicitudesEspacio.DTOCambioEstadoSEP;
import com.evtnet.evtnetback.dto.solicitudesEspacio.DTOCrearSolicitudEspacio;
import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.service.EspacioService;
import com.evtnet.evtnetback.service.SolicitudEspacioPublicoService;
import com.evtnet.evtnetback.service.SolicitudEspacioPublicoServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitudEspacio")
@AllArgsConstructor
public class SolicitudEspacioPublicoController extends BaseControllerImpl <SolicitudEspacioPublico, SolicitudEspacioPublicoServiceImpl> {
    private final SolicitudEspacioPublicoService solicitudEspacioPublicoService;

    @PostMapping("/crearSolicitudEspacio")
    public ResponseEntity crearSolicitudEspacio(@RequestBody DTOCrearSolicitudEspacio dtoSolicitudEspacio, Authentication auth){
        try{
            solicitudEspacioPublicoService.crearSolicitudEspacioPublico(dtoSolicitudEspacio, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear la solicitud de espacio público - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/buscarSolicitudesEspaciosPublicos")
    public ResponseEntity buscarSolicitudesEspaciosPublicos(@RequestBody DTOBusquedaSEP dtoBusquedaSEP, @RequestParam(name="page", defaultValue = "0")int page){
        try{
                return ResponseEntity.status(HttpStatus.OK).body(solicitudEspacioPublicoService.buscarSolicitudesEspaciosPublicos(dtoBusquedaSEP, page));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las solictudes de espacio público - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerDetalleSolicitudEP")
    public ResponseEntity obtenerDetalleSolicitudEP(@RequestParam(name="idSEP", required=true) Long idSEP){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(solicitudEspacioPublicoService.obtenerDetalleSolcitudEP(idSEP));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la solicitud de espacio público - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/cambiarEstadoSEP")
    public ResponseEntity cambiarEstadoSEP(@RequestBody DTOCambioEstadoSEP dtoCambioEstadoSEP, Authentication auth){
        try{
            solicitudEspacioPublicoService.cambiarEstadoSEP(dtoCambioEstadoSEP, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo cambiar el estado de la solicitud de espacio público - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerEspaciosParaSolicitud")
    public ResponseEntity obtenerEspaciosParaSolicitud(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(solicitudEspacioPublicoService.obtenerEspacioParaSolicitud());
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los espacios - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/vincularEspacioASolicitud")
    public ResponseEntity vincularEspacioASolicitud(@RequestParam(name="idSEP", required=true) Long idSEP, @RequestParam(name="idEspacio", required=true) Long idEspacio){
        try{
            solicitudEspacioPublicoService.vincularEspacioASolicitud(idSEP, idEspacio);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo vincular el espacio a la solicitud - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/buscarSolicitudesEspaciosPrivados")
    public ResponseEntity buscarSolicitudesEspaciosPrivados(@RequestBody DTOBusquedaSEP dtoBusquedaSEP, @RequestParam(name="page", defaultValue = "0")int page){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(solicitudEspacioPublicoService.buscarSolicitudesEspaciosPrivados(dtoBusquedaSEP, page));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las solictudes de espacio privado - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerDetalleSolicitudEPrivado")
    public ResponseEntity obtenerDetalleSolicitudEPrivado(@RequestParam(name="idSEP", required=true) Long idSEP){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(solicitudEspacioPublicoService.obtenerDetalleSolcitudEPrivado(idSEP));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la solicitud de espacio privado - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/descargarDocumentacion")
    public ResponseEntity<byte[]> descargarDocumentacionZip(@RequestParam(name="idEspacio", required=true) Long idEspacio) throws Exception {
        byte[] zipBytes = solicitudEspacioPublicoService.generarDocumentacionZip(idEspacio);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=documentacion_espacio_" + idEspacio + ".zip");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");

        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }


    @PutMapping("/cambiarEstadoSEPrivado")
    public ResponseEntity cambiarEstadoSEPrivado(@RequestBody DTOCambioEstadoSEP dtoCambioEstadoSEP, Authentication auth){
        try{
            solicitudEspacioPublicoService.cambiarEstadoSEPrivado(dtoCambioEstadoSEP, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo cambiar el estado de la solicitud de espacio privado - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}
