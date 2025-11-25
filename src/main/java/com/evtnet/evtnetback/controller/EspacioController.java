package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.Espacio;
import com.evtnet.evtnetback.service.EspacioService;
import com.evtnet.evtnetback.service.EspacioServiceImpl;
import com.evtnet.evtnetback.dto.espacios.*;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/espacios")
@AllArgsConstructor
public class EspacioController extends BaseControllerImpl <Espacio, EspacioServiceImpl> {

    private final EspacioService espacioService;

    @PostMapping(value="/crearEspacio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity crearEspacio(
            @RequestPart("espacio")DTOCrearEspacio espacio,
            @RequestPart(value="basesYCondiciones", required = false)MultipartFile basesYCondiciones,
            @RequestPart(value="documentacion", required=false)List<MultipartFile>documentacion) {
        try{
            Long espacioID = espacioService.crearEspacio(espacio, basesYCondiciones, documentacion);
            Map<String, Long> respuesta = Map.of("id", espacioID);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping(value="/crearEspacioPublico")
    public ResponseEntity crearEspacioPublico(
            @RequestBody()DTOCrearEspacio espacio) {
        try{
            Long espacioID = espacioService.crearEspacioPublico(espacio);
            Map<String, Long> respuesta = Map.of("id", espacioID);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerEspacio")
    public ResponseEntity obtenerEspacio(@RequestParam(name="id", required=true) Long id, Authentication auth){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerEspacio(id, auth.getName()));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerEspacioEditar")
    public ResponseEntity obtenerEspacioEditar(@RequestParam(name="id", required=true) Long id, Authentication auth){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerEspacioEditar(id, auth.getName()));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping(value="/editarEspacio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity editarEspacio(
            @RequestPart("espacio") DTOEspacioEditar espacio,
            @RequestPart(value="basesYCondiciones", required = false)MultipartFile basesYCondiciones,
            @RequestPart(value="documentacion", required=false)List<MultipartFile>documentacion) {
        try{
            espacioService.editarEspacio(espacio, basesYCondiciones, documentacion);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo editar el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/dejarDeAdministrar")
    public ResponseEntity dejarDeAdministrar(@RequestParam(name="espacioId", required=true) Long id, Authentication auth){
        try{
            espacioService.dejarDeAdministrar(id, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo eliminar el administrador - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerNombreEspacio")
    public ResponseEntity obtenerNombreEspacio(@RequestParam(name="id", required=true) Long id){
        try{
            String nombreEspacio=espacioService.obtenerNombreEspacio(id);
            Map<String, String> respuesta = Map.of("nombre", nombreEspacio);
            return ResponseEntity.status(HttpStatus.OK).body(respuesta);
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el nombre del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerTiposEspacio")
    public ResponseEntity obtenerTiposEspacio(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerTiposEspacio());
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los tipos de espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/buscar")
    public ResponseEntity buscarEspacios(@RequestBody DTOBusquedaEspacios dtoBusquedaEspacios){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.buscarEspacios(dtoBusquedaEspacios));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los espacios - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/buscarMisEspacios")
    public ResponseEntity buscarMisEspacios(@RequestBody DTOBusquedaMisEspacios dtoBusquedaEspacios, Authentication auth){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.buscarMisEspacios(dtoBusquedaEspacios, auth.getName()));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los espacios - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/buscarEventosPorEspacio")
    public ResponseEntity buscarEventosPorEspacio(@RequestBody DTOBusquedaEventosPorEspacio dtoBusquedaEventos){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.buscarEventosPorEspacio(dtoBusquedaEventos));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los eventos del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/buscarUsuariosNoAdministradores")
    public ResponseEntity buscarUsuariosNoAdminisradores(@RequestParam(name="idEspacio", required=true) Long id, @RequestParam("texto") String texto){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.buscarUsuariosNoAdministradores(id, texto));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los usuarios no administradores - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerAdministradoresEspacio")
    public ResponseEntity obtenerAdministradoresEspacio(@RequestParam(name="idEspacio", required=true) Long id, Authentication auth){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerAdministradoresEspacio(id, auth.getName()));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los administradores del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/eliminarAdministradorEspacio")
    public ResponseEntity eliminarAdministradorEspacio(@RequestParam(name="idEspacio", required=true) Long id, @RequestParam(name="username", required=true)String username){
        try{
            espacioService.eliminarAdministradorEspacio(id, username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo eliminar el administrador de espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/agregarAdministradorEspacio")
    public ResponseEntity agregarAdministradorEspacio(@RequestParam(name="idEspacio", required=true) Long id, @RequestParam(name="username", required=true)String username){
        try{
            espacioService.agregarAdministradorEspacio(id, username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo agregar el administrador del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/entregarPropietario")
    public ResponseEntity entregarPropietario (@RequestParam(name="idEspacio", required=true) Long id, @RequestParam(name="username", required=true)String username, Authentication auth){
        try{
            espacioService.entregarPropietario(id, username, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo entregar la propiedad del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/agregarEncargadoSubespacio")
    public ResponseEntity agregarEncargadoSubespacio(@RequestParam(name="idSubEspacio", required=true) Long id, @RequestParam(name="username", required=true)String username){
        try{
            espacioService.agregarEncargadoSubespacio(id, username);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo agregar el encargado del subespacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerEncargadosSubespacios")
    public ResponseEntity obtenerEncargadosSubespacios(@RequestParam(name="idEspacio", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerEncargadosSubespacios(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los subespacios y sus encargados - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerResenasEspacio")
    public ResponseEntity obtenerResenasEspacio(@RequestParam(name="idEspacio", required=true) Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.obtenerResenasEspacio(id));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las reseñas del espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @PostMapping("/crearResenaEspacio")
    public ResponseEntity crearResenaEspacio(@RequestBody DTOCrearResenaEspacio dtoResenaEspacio, Authentication auth){
        try{
            espacioService.crearResenaEspacio(dtoResenaEspacio, auth.getName());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear la reseña para el espacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerEstadosEspacio")
    public ResponseEntity obtenerEstadosEspacio() {
        try {
            return ResponseEntity.ok(espacioService.obtenerEstadosEspacio());
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los estados de espacios"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/buscarEspaciosPropios")
    public ResponseEntity buscarEspaciosPropios(Authentication auth){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(espacioService.buscarEspaciosPropios(auth.getName()));
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los espacios - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/actualizarCaracteristicasSubEspacio")
    public ResponseEntity actualizarCaracteristicasEspacio(@RequestBody DTOActualizarCaracteristicasSubespacio dtoCaracteristicaSubEspacio){
        try{
            espacioService.actualizarCarateristicasEspacio(dtoCaracteristicaSubEspacio);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo crear la característica para el subespacio - "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerBasesYCondiciones")
    public ResponseEntity<byte[]> obtenerBasesYCondiciones(@RequestParam Long idEspacio) throws Exception {
        byte[] pdfBytes = espacioService.obtenerBasesYCondiciones(idEspacio);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

