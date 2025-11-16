package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.mascota.DTOInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOAltaInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOModificarInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOInstanciaMascotaSecuencia;
import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.entity.InstanciaMascotaSecuencia;
import com.evtnet.evtnetback.entity.ImagenMascota;
import com.evtnet.evtnetback.entity.ParametroSistema;
import com.evtnet.evtnetback.repository.InstanciaMascotaRepository;
import com.evtnet.evtnetback.repository.InstanciaMascotaSecuenciaRepository;
import com.evtnet.evtnetback.repository.ImagenMascotaRepository;
import com.evtnet.evtnetback.repository.ParametroSistemaRepository;
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

    public InstanciaMascotaServiceImpl(InstanciaMascotaRepository instanciaMascotaRepository,
                                       InstanciaMascotaSecuenciaRepository instanciaMascotaSecuenciaRepository,
                                       ImagenMascotaRepository imagenMascotaRepository,
                                       ParametroSistemaRepository parametroSistemaRepository,
                                       ParametroSistemaService parametroSistemaService) {
        super(instanciaMascotaRepository);
        this.instanciaMascotaRepository = instanciaMascotaRepository;
        this.instanciaMascotaSecuenciaRepository = instanciaMascotaSecuenciaRepository;
        this.imagenMascotaRepository = imagenMascotaRepository;
        this.parametroSistemaRepository = parametroSistemaRepository;
        this.parametroSistemaService = parametroSistemaService;
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
                .pageRegex(im.getPage_regex())
                .selector(im.getSelector())
                .events(im.getEvents())
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
                .pageRegex(instancia.getPage_regex())
                .selector(instancia.getSelector())
                .events(instancia.getEvents())
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
        validarDatos(dto.getNombre(), dto.getDescripcion(), dto.getPageRegex(),
                dto.getSelector(), dto.getEvents(), null);

        InstanciaMascota instancia = this.save(InstanciaMascota.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .page_regex(dto.getPageRegex())
                .selector(dto.getSelector())
                .events(dto.getEvents())
                .fechaHoraAlta(LocalDateTime.now())
                .build());

        crearSecuencias(instancia, dto.getImagenes());
    }

    @Override
    @Transactional
    public void modificarInstanciaMascota(DTOModificarInstanciaMascota dto) throws Exception {
        InstanciaMascota instancia = instanciaMascotaRepository.findById(dto.getId())
                .orElseThrow(() -> new Exception("Instancia no encontrada"));

        validarDatos(dto.getNombre(), dto.getDescripcion(), dto.getPageRegex(),
                dto.getSelector(), dto.getEvents(), dto.getId());

        instancia.setNombre(dto.getNombre());
        instancia.setDescripcion(dto.getDescripcion());
        instancia.setPage_regex(dto.getPageRegex());
        instancia.setSelector(dto.getSelector());
        instancia.setEvents(dto.getEvents());
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

    private void validarDatos(String nombre, String descripcion, String pageRegex,
                              String selector, String events, Long idExcluir) throws Exception {
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

        // Validar pageRegex
        if (pageRegex == null || pageRegex.trim().isEmpty()) {
            throw new Exception("La expresión regular es obligatoria");
        }
        if (pageRegex.length() > 1000) {
            throw new Exception("La expresión regular no puede exceder 1000 caracteres");
        }

        // Validar selector
        if (selector == null || selector.trim().isEmpty()) {
            throw new Exception("El selector es obligatorio");
        }
        if (selector.length() > 1000) {
            throw new Exception("El selector no puede exceder 1000 caracteres");
        }

        // Validar events
        if (events == null || events.trim().isEmpty()) {
            throw new Exception("Los eventos son obligatorios");
        }
        if (events.length() > 1000) {
            throw new Exception("Los eventos no pueden exceder 1000 caracteres");
        }

        // Validar eventos contra parámetro del sistema
        Optional<ParametroSistema> parametroOpt = parametroSistemaRepository.findByIdentificador("eventsMascota");
        if (parametroOpt.isEmpty()) {
            throw new Exception("No se encontró el parámetro de eventos permitidos");
        }

        String[] eventosPermitidos = parametroOpt.get().getValor().split(",");
        Set<String> eventosPermitidosSet = Arrays.stream(eventosPermitidos)
                .map(String::trim)
                .collect(Collectors.toSet());

        String[] eventosIngresados = events.split(",");
        for (String evento : eventosIngresados) {
            String eventoTrim = evento.trim();
            if (!eventosPermitidosSet.contains(eventoTrim)) {
                throw new Exception("El evento '" + eventoTrim + "' no es válido");
            }
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
}