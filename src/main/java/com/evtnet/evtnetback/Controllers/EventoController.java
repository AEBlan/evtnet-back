// src/main/java/com/evtnet/evtnetback/Controllers/EventoController.java
package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.dto.comunes.CantidadResponse;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;
import com.evtnet.evtnetback.Services.EventoService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;  

    @PutMapping("/buscar")
    public ResponseEntity<List<DTOResultadoBusquedaEventos>> buscar(@RequestBody DTOBusquedaEventos filtro) throws Exception {
        return ResponseEntity.ok(service.buscar(filtro));
    }

    @PutMapping("/buscarMisEventos")
    public ResponseEntity<List<DTOResultadoBusquedaMisEventos>> buscarMisEventos(
            @RequestBody DTOBusquedaMisEventos filtro,
            Authentication authentication   // ✅ se inyecta el usuario logueado
    ) {
        String username = authentication.getName(); // ✅ nombre del usuario del token JWT
        return ResponseEntity.ok(service.buscarMisEventos(filtro, username));
    }

    @GetMapping("/obtenerEvento")
    public ResponseEntity<DTOEventoDetalle> obtenerEvento(@RequestParam long id) throws Exception {
    return ResponseEntity.ok(service.obtenerEventoDetalle(id));
    }


    @GetMapping("/obtenerDatosCreacionEvento")
    public ResponseEntity<DTODatosCreacionEvento> obtenerDatosCreacionEvento(@RequestParam Long idEspacio) throws Exception {
        return ResponseEntity.ok(service.obtenerDatosCreacionEvento(idEspacio));
    }

    @PutMapping("/pagarCreacionEvento")
    public ResponseEntity<DTOPreferenciaPago> pagarCreacionEvento(@RequestBody DTOEventoCreate req) throws Exception {
        return ResponseEntity.ok(service.pagarCreacionEvento(req));
    }

    @PostMapping("/crearEvento")
    public ResponseEntity<IdResponse> crearEvento(@RequestBody DTOEventoCreate req) throws Exception {
        long id = service.crearEvento(req);
        return ResponseEntity.ok(new IdResponse(id));
    }

    @GetMapping("/obtenerCantidadEventosSuperpuestos")
    public ResponseEntity<CantidadResponse> obtenerCantidadEventosSuperpuestos(
            @RequestParam long idEspacio,
            @RequestParam long fechaHoraDesde,
            @RequestParam long fechaHoraHasta) {
        int cantidad = service.obtenerCantidadEventosSuperpuestos(idEspacio, fechaHoraDesde, fechaHoraHasta);
        return ResponseEntity.ok(new CantidadResponse(cantidad));
    }

    // --- NUEVOS para el front actual ---

    @GetMapping("/obtenerEventoParaInscripcion")
    public ResponseEntity<DTOEventoParaInscripcion> obtenerEventoParaInscripcion(@RequestParam long id) throws Exception {
        return ResponseEntity.ok(service.obtenerEventoParaInscripcion(id));
    }

    @PutMapping("/verificarDatosPrePago")
    public ResponseEntity<DTOVerificacionPrePago> verificarDatosPrePago(@RequestBody DTOInscripcion dto) throws Exception {
        return ResponseEntity.ok(service.verificarDatosPrePago(dto));
    }

    @PostMapping("/inscribirse")
    public ResponseEntity<Void> inscribirse(@RequestBody DTOInscripcion dto) throws Exception {
        service.inscribirse(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/desinscribirse")
    public ResponseEntity<Void> desinscribirse(@RequestParam long idEvento) throws Exception {
        service.desinscribirse(idEvento);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obtenerMontoDevolucionCancelacionInscripcion")
    public ResponseEntity<Map<String, Number>> obtenerMontoDevolucionCancelacionInscripcion(
            @RequestParam long idEvento, @RequestParam String username) throws Exception {
        return ResponseEntity.ok(Map.of("monto", service.obtenerMontoDevolucionCancelacion(idEvento, username)));
    }

    @GetMapping("/obtenerDatosModificacionEvento")
    public ResponseEntity<DTOModificarEvento> obtenerDatosModificacionEvento(@RequestParam long id) throws Exception {
        return ResponseEntity.ok(service.obtenerDatosModificacionEvento(id));
    }

    @PostMapping("/modificarEvento")
    public ResponseEntity<Void> modificarEvento(@RequestBody DTOModificarEvento dto) {
        service.modificarEvento(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscarUsuariosNoInscriptos")
    public ResponseEntity<List<DTOBusquedaUsuario>> buscarUsuariosNoInscriptos(
            @RequestParam long idEvento,
            @RequestParam(required = false, defaultValue = "") String texto
    ) {
        return ResponseEntity.ok(service.buscarUsuariosNoInscriptos(idEvento, texto));
    }


    @GetMapping("/obtenerInscripciones")
    public ResponseEntity<DTOInscripcionesEvento> obtenerInscripciones(
        @RequestParam long id,
        @RequestParam(required = false) String busqueda) throws Exception {
    return ResponseEntity.ok(service.obtenerInscripciones(id, busqueda));
    }

    @DeleteMapping("/cancelarInscripcion")
    public ResponseEntity<Void> cancelarInscripcion(@RequestParam long id) {
        service.cancelarInscripcion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obtenerDatosParaInscripcion")
    public ResponseEntity<DTODatosParaInscripcion> obtenerDatosParaInscripcion(
            @RequestParam long id, Authentication auth) throws Exception {
        return ResponseEntity.ok(service.obtenerDatosParaInscripcion(id, auth.getName()));
    }

    @PostMapping("/inscribirUsuario")
    public ResponseEntity<Void> inscribirUsuario(@RequestBody DTOInscripcion dto) {
        service.inscribirUsuario(dto);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/obtenerAdministradores")
    public ResponseEntity<DTOAdministradores> obtenerAdministradores(
        @RequestParam long idEvento,
        Authentication auth) throws Exception {
    return ResponseEntity.ok(service.obtenerAdministradores(idEvento, auth.getName()));
    }

    @GetMapping("/buscarUsuariosNoAdministradores")
    public ResponseEntity<List<DTOBusquedaUsuario>> buscarUsuariosNoAdministradores(
            @RequestParam long idEvento,
            @RequestParam(required = false, defaultValue = "") String texto
    ) {
        return ResponseEntity.ok(service.buscarUsuariosNoAdministradores(idEvento, texto));
    }

    @PostMapping("/agregarAdministrador")
    public ResponseEntity<Void> agregarAdministrador(
        @RequestParam long idEvento,
        @RequestParam String username) {
    service.agregarAdministrador(idEvento, username);
    return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quitarAdministrador")
    public ResponseEntity<Void> quitarAdministrador(
        @RequestParam long idEvento,
        @RequestParam String username) {
    service.quitarAdministrador(idEvento, username);
    return ResponseEntity.ok().build();
    }

    @PutMapping("/entregarOrganizador")
    public ResponseEntity<Void> entregarOrganizador(
        @RequestParam long idEvento,
        @RequestParam String username) throws Exception {
    service.entregarOrganizador(idEvento, username);
    return ResponseEntity.ok().build();
    }

    // --- DENUNCIAS ---

    @PostMapping("/denunciar")
    public ResponseEntity<Void> denunciarEvento(@RequestBody DTODenunciaEvento dto, Authentication auth) {
        service.denunciarEvento(dto, auth.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/buscarDenuncias")
    public ResponseEntity<Page<DTODenunciaEventoSimple>> buscarDenuncias(
            @RequestBody DTOBusquedaDenunciasEventos filtro,
            @RequestParam(defaultValue = "0") int page
    ) throws Exception {
        return ResponseEntity.ok(service.buscarDenuncias(filtro, page));
    }


    @GetMapping("/obtenerDenunciaCompleta")
    public ResponseEntity<DTODenunciaEventoCompleta> obtenerDenunciaCompleta(@RequestParam long idDenuncia) throws Exception {
        return ResponseEntity.ok(service.obtenerDenunciaCompleta(idDenuncia));
    }

    @GetMapping("/obtenerDatosParaCambioEstadoDenuncia")
    public ResponseEntity<DTODatosParaCambioEstadoDenuncia> obtenerDatosParaCambioEstado(@RequestParam long idDenuncia) {
        return ResponseEntity.ok(service.obtenerDatosParaCambioEstado(idDenuncia));
    }

    @PostMapping("/cambiarEstadoDenuncia")
    public ResponseEntity<Void> cambiarEstadoDenuncia(@RequestBody DTOCambioEstadoDenuncia dto, Authentication auth) {
        service.cambiarEstadoDenuncia(dto, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDatosParaDenunciar")
    public ResponseEntity<DTODatosParaDenunciarEvento> getDatosParaDenunciar(
            @RequestParam long idEvento, Authentication auth) {
        return ResponseEntity.ok(service.obtenerDatosParaDenunciar(idEvento, auth.getName()));
    }
}
