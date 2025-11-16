package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.*;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstanciaMascotaServiceImpl extends BaseServiceImpl<InstanciaMascota, Long>
        implements InstanciaMascotaService {

    private final InstanciaMascotaRepository instanciaMascotaRepository;
    private final InstanciaMascotaSecuenciaRepository instanciaMascotaSecuenciaRepository;
    private final ImagenMascotaRepository imagenMascotaRepository;
    private final ParametroSistemaRepository parametroSistemaRepository;
    private final ParametroSistemaService parametroSistemaService;

    private final EventoMascotaRepository eventoMascotaRepository;

    public InstanciaMascotaServiceImpl(InstanciaMascotaRepository instanciaMascotaRepository,
                                       InstanciaMascotaSecuenciaRepository instanciaMascotaSecuenciaRepository,
                                       ImagenMascotaRepository imagenMascotaRepository,
                                       ParametroSistemaRepository parametroSistemaRepository,
                                       ParametroSistemaService parametroSistemaService,
                                       EventoMascotaRepository eventoMascotaRepository) {
        super(instanciaMascotaRepository);
        this.instanciaMascotaRepository = instanciaMascotaRepository;
        this.instanciaMascotaSecuenciaRepository = instanciaMascotaSecuenciaRepository;
        this.imagenMascotaRepository = imagenMascotaRepository;
        this.parametroSistemaRepository = parametroSistemaRepository;
        this.parametroSistemaService = parametroSistemaService;
        this.eventoMascotaRepository = eventoMascotaRepository;
    }

    @Override
    public Page<DTOInstanciaMascota> obtenerListaInstanciaMascota(int page, String texto) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(page, longitudPagina, Sort.by("id").ascending());

        Specification<InstanciaMascota> spec = Specification.where(null);

        if (texto != null && !texto.trim().isEmpty()) {
            String pattern = "%" + texto.toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern)
                )
            );
        }


        Page<InstanciaMascota> instancias = instanciaMascotaRepository.findAll(spec, pageable);
        return instancias.map(im -> DTOInstanciaMascota.builder()
                .id(im.getId())
                .nombre(im.getNombre())
                .descripcion(im.getDescripcion())
                .pageSelector(im.getPageSelector())
                .selector(im.getSelector())
                .eventos(im.getEventos().stream().map(e -> DTOEventoMascota.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .valor(e.getValor())
                        .build()).toList())
                .longitud(im.getInstanciaMascotaSecuencias().stream().filter(i -> i.getFechaHoraBaja() == null).toList().size())
                .fechaAlta(im.getFechaHoraAlta() == null ? null
                        : im.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaBaja(im.getFechaHoraBaja() == null ? null
                        : im.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build());
    }

    @Override
    public DTOInstanciaMascota obtenerInstanciaMascotaCompleta(Long id) throws Exception {
        InstanciaMascota instancia = instanciaMascotaRepository.findById(id)
                .orElseThrow(() -> new Exception("Instancia no encontrada"));

        List<InstanciaMascotaSecuencia> secuencias = instanciaMascotaSecuenciaRepository
                .findByInstanciaMascotaAndFechaHoraBajaIsNullOrderByOrdenAsc(instancia);

        List<DTOInstanciaMascotaSecuencia> secuenciasDTO = secuencias.stream()
                .map(s -> DTOInstanciaMascotaSecuencia.builder()
                        .texto(s.getTexto())
                        .imagenId(s.getImagenMascota() == null ? null : s.getImagenMascota().getId())
                        .orden(s.getOrden())
                        .build())
                .collect(Collectors.toList());

        return DTOInstanciaMascota.builder()
                .id(instancia.getId())
                .nombre(instancia.getNombre())
                .descripcion(instancia.getDescripcion())
                .pageSelector(instancia.getPageSelector())
                .selector(instancia.getSelector())
                .eventos(instancia.getEventos().stream().map(e -> DTOEventoMascota.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .valor(e.getValor())
                        .build()).toList())
                .longitud(secuencias.size())
                .fechaAlta(instancia.getFechaHoraAlta() == null ? null
                        : instancia.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaBaja(instancia.getFechaHoraBaja() == null ? null
                        : instancia.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .secuencia(secuenciasDTO)
                .build();
    }

    @Override
    @Transactional
    public void altaInstanciaMascota(DTOAltaInstanciaMascota dto) throws Exception {
        validarDatos(dto.getNombre(), dto.getDescripcion(), dto.getPageSelector(),
                dto.getSelector(), null);

        ArrayList<EventoMascota> eventosMascota = new ArrayList<>();

        for (Long eventoId : dto.getEventos()) {
            eventosMascota.add(eventoMascotaRepository.findById(eventoId).orElseThrow(() -> new Exception("No se encontró un evento")));
        }

        InstanciaMascota instancia = this.save(InstanciaMascota.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .pageSelector(dto.getPageSelector())
                .selector(dto.getSelector())
                .eventos(eventosMascota)
                .fechaHoraAlta(LocalDateTime.now())
                .build());

        crearSecuencias(instancia, dto.getImagenes());
    }

    @Override
    @Transactional
    public void modificarInstanciaMascota(DTOModificarInstanciaMascota dto) throws Exception {
        InstanciaMascota instancia = instanciaMascotaRepository.findById(dto.getId())
                .orElseThrow(() -> new Exception("Instancia no encontrada"));

        validarDatos(dto.getNombre(), dto.getDescripcion(), dto.getPageSelector(),
                dto.getSelector(), dto.getId());

        ArrayList<EventoMascota> eventosMascota = new ArrayList<>();

        for (Long eventoId : dto.getEventos()) {
            eventosMascota.add(eventoMascotaRepository.findById(eventoId).orElseThrow(() -> new Exception("No se encontró un evento")));
        }

        instancia.setNombre(dto.getNombre());
        instancia.setDescripcion(dto.getDescripcion());
        instancia.setPageSelector(dto.getPageSelector());
        instancia.setSelector(dto.getSelector());
        instancia.setEventos(eventosMascota);
        this.save(instancia);

        // Check if secuencia changed
        List<InstanciaMascotaSecuencia> secuenciasActuales = instanciaMascotaSecuenciaRepository
                .findByInstanciaMascotaAndFechaHoraBajaIsNullOrderByOrdenAsc(instancia);

        if (secuenciasCambiaron(secuenciasActuales, dto.getImagenes())) {
            // Dar de baja secuencias actuales
            LocalDateTime ahora = LocalDateTime.now();
            for (InstanciaMascotaSecuencia sec : secuenciasActuales) {
                sec.setFechaHoraBaja(ahora);
                instanciaMascotaSecuenciaRepository.save(sec);
            }

            // Crear nuevas secuencias
            crearSecuencias(instancia, dto.getImagenes());
        }
    }

    @Override
    public void bajaInstanciaMascota(Long id) throws Exception {
        InstanciaMascota instanciaMascota = instanciaMascotaRepository.findById(id).orElseThrow(() -> new Exception("Instancia no encontrada"));
        instanciaMascota.setFechaHoraBaja(LocalDateTime.now());
        instanciaMascotaRepository.save(instanciaMascota);
    }

    @Override
    public List<DTOEventoMascota> obtenerEventosMascota() throws Exception {
        return eventoMascotaRepository.findAll().stream().filter(e -> e.getFechaHoraBaja() == null).map(e -> DTOEventoMascota.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .valor(e.getValor())
                .build()).toList();
    }

    private void validarDatos(String nombre, String descripcion, String pageSelector,
                              String selector, Long idExcluir) throws Exception {
        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
        if (nombre.length() > 50) {
            throw new Exception("El nombre no puede exceder 50 caracteres");
        }

        // Verificar nombre único
        Optional<InstanciaMascota> existente = instanciaMascotaRepository.findByNombre(nombre);
        if (existente.isPresent() && (idExcluir == null || !existente.get().getId().equals(idExcluir))) {
            throw new Exception("Ya existe otra instancia con este nombre, ingrese un nombre distinto para la instancia");
        }

        // Validar descripción
        if (descripcion != null && descripcion.length() > 1000) {
            throw new Exception("La descripción no puede exceder 1000 caracteres");
        }

        // Validar pageSelector
        validarPageSelector(pageSelector);

        // Validar selector
        if (selector == null || selector.trim().isEmpty()) {
            throw new Exception("El selector es obligatorio");
        }
        if (selector.length() > 1000) {
            throw new Exception("El selector no puede exceder 1000 caracteres");
        }
    }

    private void crearSecuencias(InstanciaMascota instancia, List<?> items) throws Exception {
        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            String texto;
            Long imagenId;

            if (item instanceof DTOAltaInstanciaMascota.SecuenciaItem) {
                DTOAltaInstanciaMascota.SecuenciaItem altaItem = (DTOAltaInstanciaMascota.SecuenciaItem) item;
                texto = altaItem.getTexto();
                imagenId = altaItem.getImagenId();
            } else {
                DTOModificarInstanciaMascota.SecuenciaItem modItem = (DTOModificarInstanciaMascota.SecuenciaItem) item;
                texto = modItem.getTexto();
                imagenId = modItem.getImagenId();
            }

            ImagenMascota imagen = null;
            if (imagenId != null) {
                imagen = imagenMascotaRepository.findById(imagenId)
                        .orElseThrow(() -> new Exception("Imagen no encontrada"));
            }

            InstanciaMascotaSecuencia secuencia = InstanciaMascotaSecuencia.builder()
                    .instanciaMascota(instancia)
                    .imagenMascota(imagen)
                    .texto(texto)
                    .orden(i + 1)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();

            instanciaMascotaSecuenciaRepository.save(secuencia);
        }
    }

    private boolean secuenciasCambiaron(List<InstanciaMascotaSecuencia> actuales, List<?> nuevas) {
        if (actuales.size() != nuevas.size()) {
            return true;
        }

        for (int i = 0; i < actuales.size(); i++) {
            InstanciaMascotaSecuencia actual = actuales.get(i);
            Object item = nuevas.get(i);

            String nuevoTexto;
            Long nuevaImagenId;

            if (item instanceof DTOAltaInstanciaMascota.SecuenciaItem) {
                DTOAltaInstanciaMascota.SecuenciaItem altaItem = (DTOAltaInstanciaMascota.SecuenciaItem) item;
                nuevoTexto = altaItem.getTexto();
                nuevaImagenId = altaItem.getImagenId();
            } else {
                DTOModificarInstanciaMascota.SecuenciaItem modItem = (DTOModificarInstanciaMascota.SecuenciaItem) item;
                nuevoTexto = modItem.getTexto();
                nuevaImagenId = modItem.getImagenId();
            }

            Long actualImagenId = actual.getImagenMascota() == null ? null : actual.getImagenMascota().getId();

            if (!Objects.equals(actual.getTexto(), nuevoTexto) ||
                    !Objects.equals(actualImagenId, nuevaImagenId)) {
                return true;
            }
        }

        return false;
    }

    private void validarPageSelector(String pageSelector) throws Exception {
        if (pageSelector == null || pageSelector.trim().isEmpty()) {
            throw new Exception("El selector de páginas es obligatorio");
        }
        if (pageSelector.length() > 1000) {
            throw new Exception("El selector de páginas no puede exceder 1000 caracteres");
        }

        // Validate each pattern
        String[] patterns = pageSelector.split(",");
        for (String pattern : patterns) {
            String trimmed = pattern.trim().replaceAll("^/|/$", "");
            if (trimmed.isEmpty()) {
                throw new Exception("El selector contiene patrones vacíos");
            }
        }
    }

    private boolean urlMatchesPageSelector(String url, String pageSelector) {
        try {
            // Extract path from URL
            String path = url.trim();
            path = path.replaceFirst("^https?://[^/]+", ""); // Remove protocol and domain
            path = path.split("\\?")[0]; // Remove query params
            path = path.replaceAll("^/|/$", ""); // Remove leading/trailing slashes

            // Check against each pattern
            String[] patterns = pageSelector.split(",");
            for (String pattern : patterns) {
                String trimmed = pattern.trim().replaceAll("^/|/$", "");
                String regexPattern = "^" + trimmed.replace("*", ".*") + "$";
                if (path.matches(regexPattern)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}