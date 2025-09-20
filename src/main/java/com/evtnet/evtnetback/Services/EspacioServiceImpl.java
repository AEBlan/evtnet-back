package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Entities.DisciplinaEspacio;
import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.DisciplinaEspacioRepository;
import com.evtnet.evtnetback.Repositories.DisciplinaRepository;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.dto.espacios.DTOCrearEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioDetalle;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.evtnet.evtnetback.Entities.TipoEspacio;
import com.evtnet.evtnetback.Repositories.TipoEspacioRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Service
public class EspacioServiceImpl extends BaseServiceImpl<Espacio, Long> implements EspacioService {

    private final EspacioRepository espacioRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaEspacioRepository disciplinaEspacioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoEspacioRepository tipoEspacioRepository;

    @Value("${app.espacios.tipo-privado.nombre:Privado}")
    private String tipoPrivadoNombre;


  public EspacioServiceImpl(
        EspacioRepository espacioRepository,
        DisciplinaRepository disciplinaRepository,
        DisciplinaEspacioRepository disciplinaEspacioRepository,
        TipoEspacioRepository tipoEspacioRepository,
        UsuarioRepository usuarioRepository
    ) {
    super(espacioRepository);
    this.espacioRepository = espacioRepository;
    this.disciplinaRepository = disciplinaRepository;
    this.disciplinaEspacioRepository = disciplinaEspacioRepository;
    this.tipoEspacioRepository = tipoEspacioRepository;
    this.usuarioRepository = usuarioRepository;
    }


  @Override
@Transactional
public IdResponse crearEspacioPrivado(DTOCrearEspacio dto, Long usuarioActualId) {
    validarCrear(dto);

    final String nombre = dto.nombre().trim();
    final String dir    = dto.direccion().trim();

    // Duplicado por nombre + dirección
    if (espacioRepository.existeDuplicadoNombreDireccion(nombre, dir)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un espacio con el mismo nombre y dirección");
    }

    // Propietario (usuario autenticado)
    var usuario = usuarioRepository.findById(usuarioActualId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

    // TipoEspacio PRIVADO
    TipoEspacio tipoPrivado = resolveTipoEspacioPrivado();

    // ---- Crear usando SETTERS ----
    Espacio e = new Espacio();
    e.setNombre(nombre);
    e.setDescripcion(dto.descripcion());
    e.setDireccionUbicacion(dir);
    e.setLatitudUbicacion(java.math.BigDecimal.valueOf(dto.latitud()));
    e.setLongitudUbicacion(java.math.BigDecimal.valueOf(dto.longitud()));
    e.setPropietario(usuario);              // <- propietario directo
    e.setTipoEspacio(tipoPrivado);          // <- privado por tipo
    e.setSolicitudEspacioPublico(null);     // <- privado por defecto (opcional)
    e.setFechaHoraAlta(java.time.LocalDateTime.now());

    // Guardar
    e = espacioRepository.save(e);

    // Vincular disciplinas si vinieron
    if (dto.disciplinas() != null && !dto.disciplinas().isEmpty()) {
        var disciplinas = disciplinaRepository.findAllById(dto.disciplinas());
        if (disciplinas.size() != dto.disciplinas().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alguna disciplina no existe");
        }
        for (Disciplina d : disciplinas) {
            var de = new DisciplinaEspacio();
            de.setEspacio(e);
            de.setDisciplina(d);
            disciplinaEspacioRepository.save(de);
        }
    }

    return new IdResponse(e.getId());
}


    @Override
    @Transactional(readOnly = true)
    public DTOEspacioDetalle detalle(Long espacioId) {
        Espacio e = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new EntityNotFoundException("Espacio no encontrado"));

        // Map mínimo al detalle
        return new DTOEspacioDetalle(
                e.getNombre(),
                e.getTipoEspacio() != null ? e.getTipoEspacio().getNombre() : null,
                e.getDescripcion(),
                e.getDireccionUbicacion(),
                e.getLatitudUbicacion() != null ? e.getLatitudUbicacion().doubleValue() : 0.0,
                e.getLongitudUbicacion() != null ? e.getLongitudUbicacion().doubleValue() : 0.0,
                0, // cantidadImagenes (completar si tiene relación)
                java.util.Collections.emptyList(), // disciplinas (puede proyectar desde DisciplinaEspacio)
                java.util.Collections.emptyList(), // caracteristicas
                false, // esAdmin (depende de seguridad)
                null   // idChat (si aplica)
        );
    }

    // -------- Validaciones para #US_ESP_1 --------
    private void validarCrear(DTOCrearEspacio dto) {
        if (dto == null) throw bad("Payload requerido");
        if (dto.nombre() == null || dto.nombre().isBlank()) throw bad("El nombre es obligatorio");
        if (dto.nombre().length() > 50) throw bad("El nombre no debe superar 50 caracteres");
        if (dto.direccion() == null || dto.direccion().isBlank()) throw bad("La dirección es obligatoria");
        if (dto.direccion().length() > 50) throw bad("La dirección no debe superar 50 caracteres");
        if (dto.descripcion() != null && dto.descripcion().length() > 500) throw bad("La descripción no debe superar 500 caracteres");
        if (dto.latitud() == null || dto.longitud() == null) throw bad("Debe indicar la ubicación (lat/lon)");
    }

    // Validacion para tipo de espacio
    private TipoEspacio resolveTipoEspacioPrivado() {
    // Caso por nombre (default = "Privado")
    return tipoEspacioRepository.findByNombreIgnoreCase(tipoPrivadoNombre.trim())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No existe TipoEspacio con nombre: " + tipoPrivadoNombre));
}


    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}

