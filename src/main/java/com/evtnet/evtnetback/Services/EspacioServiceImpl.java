package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.dto.espacios.DTOCrearEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioDetalle;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Service
public class EspacioServiceImpl extends BaseServiceImpl<Espacio, Long> implements EspacioService {

    private final EspacioRepository espacioRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepository;
    private final SubEspacioRepository subEspacioRepository;
    private final AdministradorEspacioRepository administradorEspacioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoEspacioRepository tipoEspacioRepository;

    @Value("${app.espacios.tipo-privado.nombre:Privado}")
    private String tipoPrivadoNombre;

    public EspacioServiceImpl(
            EspacioRepository espacioRepository,
            DisciplinaRepository disciplinaRepository,
            DisciplinaSubEspacioRepository disciplinaSubEspacioRepository,
            SubEspacioRepository subEspacioRepository,
            TipoEspacioRepository tipoEspacioRepository,
            AdministradorEspacioRepository administradorEspacioRepository,
            UsuarioRepository usuarioRepository
    ) {
        super(espacioRepository);
        this.espacioRepository = espacioRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaSubEspacioRepository = disciplinaSubEspacioRepository;
        this.subEspacioRepository = subEspacioRepository;
        this.tipoEspacioRepository = tipoEspacioRepository;
        this.administradorEspacioRepository = administradorEspacioRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public IdResponse crearEspacioPrivado(DTOCrearEspacio dto, Long usuarioActualId) {
        validarCrear(dto);

        final String nombre = dto.nombre().trim();
        final String dir = dto.direccion().trim();

        Espacio e = new Espacio();
        e.setNombre(nombre);
        e.setDescripcion(dto.descripcion());
        e.setDireccionUbicacion(dir);
        e.setLatitudUbicacion(BigDecimal.valueOf(dto.latitud()));
        e.setLongitudUbicacion(BigDecimal.valueOf(dto.longitud()));

        var usuario = usuarioRepository.findById(usuarioActualId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

        // Tipo de espacio PRIVADO
        TipoEspacio tipoPrivado = resolveTipoEspacioPrivado();
        e.setTipoEspacio(tipoPrivado);
        e.setSolicitudEspacioPublico(null);
        e = espacioRepository.save(e);

        // Crear un SubEspacio base
        SubEspacio sub = new SubEspacio();
        sub.setNombre(e.getNombre() + " - principal");
        sub.setDescripcion("Subespacio base del espacio privado");
        sub.setEspacio(e);
        sub.setFechaHoraAlta(java.time.LocalDateTime.now());
        sub = subEspacioRepository.save(sub);

        // Vincular disciplinas al SubEspacio
        if (dto.disciplinas() != null && !dto.disciplinas().isEmpty()) {
            var disciplinas = disciplinaRepository.findAllById(dto.disciplinas());
            if (disciplinas.size() != dto.disciplinas().size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alguna disciplina no existe");
            }
            for (Disciplina d : disciplinas) {
                var de = new DisciplinaSubEspacio();
                de.setSubEspacio(sub); 
                de.setDisciplina(d);
                disciplinaSubEspacioRepository.save(de);
            }
        }

        return new IdResponse(e.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public DTOEspacioDetalle detalle(Long espacioId) {
        Espacio e = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new EntityNotFoundException("Espacio no encontrado"));

        return new DTOEspacioDetalle(
                e.getNombre(),
                e.getTipoEspacio() != null ? e.getTipoEspacio().getNombre() : null,
                e.getDescripcion(),
                e.getDireccionUbicacion(),
                e.getLatitudUbicacion() != null ? e.getLatitudUbicacion().doubleValue() : 0.0,
                e.getLongitudUbicacion() != null ? e.getLongitudUbicacion().doubleValue() : 0.0,
                0,
                java.util.Collections.emptyList(),
                java.util.Collections.emptyList(),
                false,
                null
        );
    }

    private void validarCrear(DTOCrearEspacio dto) {
        if (dto == null) throw bad("Payload requerido");
        if (dto.nombre() == null || dto.nombre().isBlank()) throw bad("El nombre es obligatorio");
        if (dto.nombre().length() > 50) throw bad("El nombre no debe superar 50 caracteres");
        if (dto.direccion() == null || dto.direccion().isBlank()) throw bad("La dirección es obligatoria");
        if (dto.direccion().length() > 50) throw bad("La dirección no debe superar 50 caracteres");
        if (dto.descripcion() != null && dto.descripcion().length() > 500) throw bad("La descripción no debe superar 500 caracteres");
        if (dto.latitud() == null || dto.longitud() == null) throw bad("Debe indicar la ubicación (lat/lon)");
    }

    private TipoEspacio resolveTipoEspacioPrivado() {
        return tipoEspacioRepository.findByNombreIgnoreCase(tipoPrivadoNombre.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "No existe TipoEspacio con nombre: " + tipoPrivadoNombre));
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
