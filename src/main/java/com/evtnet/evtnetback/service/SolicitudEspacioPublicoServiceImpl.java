package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.espacios.DTOArchivo;
import com.evtnet.evtnetback.dto.espacios.DTOEstadoEspacio;
import com.evtnet.evtnetback.dto.solicitudesEspacio.*;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SolicitudEspacioPublicoServiceImpl extends BaseServiceImpl <SolicitudEspacioPublico, Long> implements SolicitudEspacioPublicoService {

    @Value("${app.storage.documentacion:/app/storage/documentacion}")
    private String directorioBase;

    @Value("${app.storage.perfiles:/app/storage/perfiles}")
    private String directorioPerfiles;

    private final SolicitudEspacioPublicoRepository solicitudEspacioPublicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final SEPEstadoRepository sepEstadoRepository;
    private final EstadoSEPRepository estadoSEPRepository;
    private final EspacioRepository espacioRepository;
    private final EspacioEstadoRepository espacioEstadoRepository;
    private final EstadoEspacioRepository estadoEspacioRepository;
    private final RegistroSingleton registroSingleton;
    private final ParametroSistemaService parametroSistemaService;

    public SolicitudEspacioPublicoServiceImpl(
            SolicitudEspacioPublicoRepository solicitudEspacioPublicoRepository,
            UsuarioRepository usuarioRepository,
            SEPEstadoRepository sepEstadoRepository,
            EstadoSEPRepository estadoSEPRepository,
            EspacioRepository espacioRepository,
            EspacioEstadoRepository espacioEstadoRepository,
            EstadoEspacioRepository estadoEspacioRepository,
            RegistroSingleton registroSingleton,
            ParametroSistemaService parametroSistemaService
    ) {
        super(solicitudEspacioPublicoRepository);
        this.solicitudEspacioPublicoRepository = solicitudEspacioPublicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.sepEstadoRepository = sepEstadoRepository;
        this.estadoSEPRepository = estadoSEPRepository;
        this.espacioRepository = espacioRepository;
        this.espacioEstadoRepository = espacioEstadoRepository;
        this.estadoEspacioRepository = estadoEspacioRepository;
        this.registroSingleton = registroSingleton;
        this.parametroSistemaService = parametroSistemaService;
    }

    @Override
    public void crearSolicitudEspacioPublico(DTOCrearSolicitudEspacio dtoSolicitud, String username)throws Exception{
        validarDatosSolicitud(dtoSolicitud);

        Usuario usuario=this.usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));;

        EstadoSEP estado=this.estadoSEPRepository.findByNombre("Pendiente");

        SolicitudEspacioPublico solicitudEspacioPublico=SolicitudEspacioPublico.builder()
                .nombreEspacio(dtoSolicitud.getNombre())
                .descripcion(dtoSolicitud.getDescripcion())
                .direccionUbicacion(dtoSolicitud.getDireccion())
                .longitudUbicacion(new BigDecimal(dtoSolicitud.getLongitud()))
                .latitudUbicacion(new BigDecimal(dtoSolicitud.getLatitud()))
                .justificacion(dtoSolicitud.getJustificacion())
                .fechaHoraAlta(LocalDateTime.now())
                .solicitante(usuario)
                .build();

        solicitudEspacioPublico=save(solicitudEspacioPublico);

        SEPEstado sepEstado= SEPEstado.builder()
                .solicitudEspacioPublico(solicitudEspacioPublico)
                .estadoSEP(estado)
                .responsable(usuario)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        sepEstado=this.sepEstadoRepository.save(sepEstado);
        registroSingleton.write("Espacios", "solicitud_espacio_publico", "creacion", "SEP de ID " + solicitudEspacioPublico.getId());
        registroSingleton.write("Espacios", "solicitud_espacio_publico", "creacion", "SEPEstado de ID " + sepEstado.getId());
    }

    @Override
    public Page<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPublicos(DTOBusquedaSEP dtoBusquedaSEP, int page) throws Exception {

        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(page, longitudPagina);

        Set<SolicitudEspacioPublico> resultadoFinal = new HashSet<>();
        List<Set<SolicitudEspacioPublico>> setsPorFiltro = new ArrayList<>();

        if (dtoBusquedaSEP.getUbicacion() != null) {
            double rangoMetros = dtoBusquedaSEP.getUbicacion().getRango();
            double gradosPorMetro = 1.0 / 111_320.0;
            double rangoGrados = rangoMetros * gradosPorMetro;

            double latDesde = dtoBusquedaSEP.getUbicacion().getLatitud() - rangoGrados;
            double latHasta = dtoBusquedaSEP.getUbicacion().getLatitud() + rangoGrados;
            double lonDesde = dtoBusquedaSEP.getUbicacion().getLongitud() - rangoGrados;
            double lonHasta = dtoBusquedaSEP.getUbicacion().getLongitud() + rangoGrados;

            setsPorFiltro.add(new HashSet<>(
                    solicitudEspacioPublicoRepository.findSolicitudesByUbicacion(
                            new BigDecimal(latDesde),
                            new BigDecimal(latHasta),
                            new BigDecimal(lonDesde),
                            new BigDecimal(lonHasta)
                    )
            ));
        }

        if (dtoBusquedaSEP.getTexto() != null && !dtoBusquedaSEP.getTexto().isBlank()) {
            String[] palabras = dtoBusquedaSEP.getTexto().split(" ");
            Set<SolicitudEspacioPublico> porTexto = new HashSet<>();

            for (String palabra : palabras) {
                if (palabra.length() > 2) {
                    porTexto.addAll(this.solicitudEspacioPublicoRepository.findSolicitudesByTexto(palabra));
                }
            }
            setsPorFiltro.add(porTexto);
        }

        if (dtoBusquedaSEP.getEspacios() != null && !dtoBusquedaSEP.getEspacios().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(
                    this.solicitudEspacioPublicoRepository.findSolicitudesByEspacio(dtoBusquedaSEP.getEspacios())
            ));
        }

        if (dtoBusquedaSEP.getEstados() != null && !dtoBusquedaSEP.getEstados().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(
                    this.solicitudEspacioPublicoRepository.findSolicitudesByEstado(dtoBusquedaSEP.getEstados())
            ));
        }

        if (dtoBusquedaSEP.getFechaIngresoDesde() != null || dtoBusquedaSEP.getFechaIngresoHasta() != null) {
            LocalDate fechaIngresoDesde = dtoBusquedaSEP.getFechaIngresoDesde() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaIngresoDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;

            LocalDate fechaIngresoHasta = dtoBusquedaSEP.getFechaIngresoHasta() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaIngresoHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            setsPorFiltro.add(new HashSet<>(
                    this.solicitudEspacioPublicoRepository.findSolicitudesByFechaIngreso(fechaIngresoDesde, fechaIngresoHasta)
            ));
        }

        if (dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde() != null || dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta() != null) {
            LocalDate fechaUltimoCambioEstadoDesde = dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;

            LocalDate fechaUltimoCambioEstadoHasta = dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            setsPorFiltro.add(new HashSet<>(
                    this.solicitudEspacioPublicoRepository.findSolicitudesByFechaCambioEstado(fechaUltimoCambioEstadoDesde, fechaUltimoCambioEstadoHasta)
            ));
        }

        if (setsPorFiltro.isEmpty()) {
            resultadoFinal.addAll(this.solicitudEspacioPublicoRepository.findAll());
        } else {
            resultadoFinal.addAll(setsPorFiltro.get(0));

            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i));
            }
        }

        List<DTOResultadoBusquedaSEP> sepDto = resultadoFinal.stream()
                .map(solicitud -> {
                    SEPEstado sepEstado = solicitud.getSepEstados().get(solicitud.getSepEstados().size() - 1);
                    return DTOResultadoBusquedaSEP.builder()
                            .idSEP(solicitud.getId())
                            .nombreEspacio(solicitud.getNombreEspacio())
                            .estado(sepEstado.getEstadoSEP().getNombre())
                            .fechaIngreso(solicitud.getFechaHoraAlta() == null ? null
                                    : solicitud.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaUltimoCambioEstado(sepEstado.getFechaHoraAlta() == null ? null
                                    : sepEstado.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .idEspacio(solicitud.getEspacio() != null ? solicitud.getEspacio().getId() : null)
                            .build();
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sepDto.size());

        List<DTOResultadoBusquedaSEP> pageContent =
                start > sepDto.size() ? Collections.emptyList() : sepDto.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sepDto.size());
    }


    @Override
    public DTOSolicitudCompleta obtenerDetalleSolcitudEP(Long idSEP)throws Exception{
        SolicitudEspacioPublico solicitudEspacioPublico=this.solicitudEspacioPublicoRepository.findById(idSEP).orElseThrow(() -> new Exception("Solicitud no encontrada"));
        DTOSolicitudCompleta dtoSolicitudCompleta=DTOSolicitudCompleta.builder()
                .idSEP(solicitudEspacioPublico.getId())
                .nombreEspacio(solicitudEspacioPublico.getNombreEspacio())
                .descripcion(solicitudEspacioPublico.getDescripcion())
                .direccion(solicitudEspacioPublico.getDireccionUbicacion())
                .longitud(solicitudEspacioPublico.getLongitudUbicacion().doubleValue())
                .latitud(solicitudEspacioPublico.getLatitudUbicacion().doubleValue())
                .justificacion(solicitudEspacioPublico.getJustificacion())
                .fechaIngreso(solicitudEspacioPublico.getFechaHoraAlta() == null ? null
                        : solicitudEspacioPublico.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .idEspacio(solicitudEspacioPublico.getEspacio()!=null ? solicitudEspacioPublico.getEspacio().getId() : null)
                .build();

        DTOSolicitudCompleta.Solicitante solicitante= DTOSolicitudCompleta.Solicitante.builder()
                .nombre(solicitudEspacioPublico.getSolicitante().getNombre())
                .apellido(solicitudEspacioPublico.getSolicitante().getApellido())
                .username(solicitudEspacioPublico.getSolicitante().getUsername())
                .email(solicitudEspacioPublico.getSolicitante().getMail())
                .build();

        if(solicitudEspacioPublico.getSolicitante().getFotoPerfil()!=null){
            Path path = Paths.get(directorioPerfiles);
            String base64Image = encodeFileToBase64(path.resolve(solicitudEspacioPublico.getSolicitante().getFotoPerfil()).toAbsolutePath().toString());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            solicitante.setContentType(contentType);
            solicitante.setUrlFotoPerfil(base64Data);
        } else {
            File file = new File(getClass().getResource("/default.png").getFile());
            Path path = file.toPath();

            String base64Image = encodeFileToBase64(path.toAbsolutePath().toString());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            solicitante.setContentType(contentType);
            solicitante.setUrlFotoPerfil(base64Data);
        }

        dtoSolicitudCompleta.setSolicitante(solicitante);

        List<SEPEstado> sepEstados=this.sepEstadoRepository.findAllBySEP(idSEP);
        List<DTOSolicitudCompleta.SEPEstado>dtoSEPEstados=new ArrayList<>();
        for(SEPEstado sepEstado:sepEstados){
            DTOSolicitudCompleta.Solicitante responsable= DTOSolicitudCompleta.Solicitante.builder()
                    .nombre(sepEstado.getResponsable().getNombre())
                    .apellido(sepEstado.getResponsable().getApellido())
                    .username(sepEstado.getResponsable().getUsername())
                    .email(sepEstado.getResponsable().getMail())
                    .build();
            if(sepEstado.getResponsable().getFotoPerfil()!=null){
                Path path = Paths.get(directorioPerfiles);
                String base64Image = encodeFileToBase64(path.resolve(sepEstado.getResponsable().getFotoPerfil()).toAbsolutePath().toString());
                String[] parts = base64Image.split(",");
                String base64Data = parts[1];
                String mimeType = parts[0].split(";")[0].split(":")[1];
                String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                responsable.setContentType(contentType);
                responsable.setUrlFotoPerfil(base64Data);
            } else {
                File file = new File(getClass().getResource("/default.png").getFile());
                Path path = file.toPath();

                String base64Image = encodeFileToBase64(path.toAbsolutePath().toString());
                String[] parts = base64Image.split(",");
                String base64Data = parts[1];
                String mimeType = parts[0].split(";")[0].split(":")[1];
                String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                responsable.setContentType(contentType);
                responsable.setUrlFotoPerfil(base64Data);
            }
            dtoSEPEstados.add(DTOSolicitudCompleta.SEPEstado.builder()
                            .id(sepEstado.getId())
                            .nombre(sepEstado.getEstadoSEP().getNombre())
                            .descripcion(sepEstado.getDescripcion())
                            .fechaHoraDesde(sepEstado.getFechaHoraAlta() == null ? null
                                    : sepEstado.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .idEstado(sepEstado.getEstadoSEP().getId())
                    .responsable(responsable)
                    .build());
        }

        dtoSolicitudCompleta.setSepEstados(dtoSEPEstados);

        return dtoSolicitudCompleta;
    }

    @Override
    public void cambiarEstadoSEP(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception{
        SolicitudEspacioPublico solicitudEspacioPublico=this.solicitudEspacioPublicoRepository.findById(dtoCambioEstado.getIdSEP()).orElseThrow(() -> new Exception("Solicitud no encontrada"));
        EstadoSEP estadoSEP=this.estadoSEPRepository.findById(dtoCambioEstado.getIdEstado()).orElseThrow(() -> new Exception("Estado no encontrado"));
        Usuario usuario=this.usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));
        SEPEstado sepEstado=this.sepEstadoRepository.findUltimoBySEP(dtoCambioEstado.getIdSEP());
        sepEstado.setFechaHoraBaja(LocalDateTime.now());
        this.sepEstadoRepository.save(sepEstado);
        SEPEstado sepEstadoNuevo=SEPEstado.builder()
                .solicitudEspacioPublico(solicitudEspacioPublico)
                .estadoSEP(estadoSEP)
                .descripcion(dtoCambioEstado.getDescripcion())
                .responsable(usuario)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        sepEstadoNuevo=this.sepEstadoRepository.save(sepEstadoNuevo);
        registroSingleton.write("Espacios", "solicitud_espacio_publico", "modificacion", "SEPEstado de ID " + sepEstado.getId());
        registroSingleton.write("Espacios", "solicitud_espacio_publico", "creacion", "SEPEstado de ID " + sepEstadoNuevo.getId());
    }

    @Override
    public List<DTOEspacioPublico> obtenerEspacioParaSolicitud()throws Exception{
        List<Espacio>espaciosPublicos=this.espacioRepository.findPublicos();
        List<DTOEspacioPublico> dtoEspaciosPublicos=new ArrayList<>();
        for (Espacio espacio : espaciosPublicos){
            dtoEspaciosPublicos.add(DTOEspacioPublico.builder()
                            .id(espacio.getId())
                            .nombre(espacio.getNombre())
                    .build());
        }
        return dtoEspaciosPublicos;
    }

    @Override
    public void vincularEspacioASolicitud(Long idSEP, Long idEspacio)throws Exception{
        Espacio espacio = this.espacioRepository.findById(idEspacio)
                .orElseThrow(() -> new Exception("Espacio no encontrado"));
        SolicitudEspacioPublico solicitud=this.solicitudEspacioPublicoRepository.findById(idSEP).orElseThrow(() -> new Exception("Solicitud no encontrada"));
        solicitud.setEspacio(espacio);
        this.solicitudEspacioPublicoRepository.save(solicitud);
        registroSingleton.write("Espacios", "solicitud_espacio_publico", "modificacion", "SEP de ID " + solicitud.getId());
    }

    @Override
    public Page<DTOResultadoBusquedaSEP> buscarSolicitudesEspaciosPrivados(DTOBusquedaSEP dtoBusquedaSEP, int page) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHoraBaja"),
                        Sort.Order.asc("fechaHoraAlta")
                )
        );

        Set<Espacio> resultadoFinal = new HashSet<>();
        List<Set<Espacio>> setsPorFiltro = new ArrayList<>();

        if (dtoBusquedaSEP.getUbicacion() != null) {
            double rangoMetros = dtoBusquedaSEP.getUbicacion().getRango();
            double gradosPorMetro = 1.0 / 111_320.0;
            double rangoGrados = rangoMetros * gradosPorMetro;

            double latDesde = dtoBusquedaSEP.getUbicacion().getLatitud() - rangoGrados;
            double latHasta = dtoBusquedaSEP.getUbicacion().getLatitud() + rangoGrados;
            double lonDesde = dtoBusquedaSEP.getUbicacion().getLongitud() - rangoGrados;
            double lonHasta = dtoBusquedaSEP.getUbicacion().getLongitud() + rangoGrados;

            setsPorFiltro.add(new HashSet<>(
                    this.espacioRepository.findEspaciosByUbicacionSolicitud(
                            new BigDecimal(latDesde),
                            new BigDecimal(latHasta),
                            new BigDecimal(lonDesde),
                            new BigDecimal(lonHasta)
                    )
            ));
        }

        if (dtoBusquedaSEP.getTexto() != null && !dtoBusquedaSEP.getTexto().isBlank()) {
            String[] palabras = dtoBusquedaSEP.getTexto().split(" ");
            Set<Espacio> porTexto = new HashSet<>();

            for (String palabra : palabras) {
                if (palabra.length() > 2) {
                    porTexto.addAll(this.espacioRepository.findEspaciosByTextoSolicitud(palabra));
                }
            }
            setsPorFiltro.add(porTexto);
        }

        if (dtoBusquedaSEP.getEstados() != null && !dtoBusquedaSEP.getEstados().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(
                    this.espacioRepository.findEspaciosByEstado(dtoBusquedaSEP.getEstados())
            ));
        }

        if (dtoBusquedaSEP.getFechaIngresoDesde() != null || dtoBusquedaSEP.getFechaIngresoHasta() != null) {
            LocalDate fechaIngresoDesde = dtoBusquedaSEP.getFechaIngresoDesde() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaIngresoDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;

            LocalDate fechaIngresoHasta = dtoBusquedaSEP.getFechaIngresoHasta() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaIngresoHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            setsPorFiltro.add(new HashSet<>(
                    this.espacioRepository.findEspaciosByFechaIngreso(fechaIngresoDesde, fechaIngresoHasta)
            ));
        }

        if (dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde() != null || dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta() != null) {
            LocalDate fechaUltimoCambioEstadoDesde = dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaUltimoCambioEstadoDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;

            LocalDate fechaUltimoCambioEstadoHasta = dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta() != null
                    ? Instant.ofEpochMilli(dtoBusquedaSEP.getFechaUltimoCambioEstadoHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            setsPorFiltro.add(new HashSet<>(
                    this.espacioRepository.findEspaciosByFechaCambioEstado(fechaUltimoCambioEstadoDesde, fechaUltimoCambioEstadoHasta)
            ));
        }

        if (setsPorFiltro.isEmpty()) {
            resultadoFinal.addAll(this.espacioRepository.findAllPrivados());
        } else {
            resultadoFinal.addAll(setsPorFiltro.get(0));
            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i));
            }
        }

        List<DTOResultadoBusquedaSEP> sepDto = resultadoFinal.stream()
                .map(espacio -> {
                    EspacioEstado espacioEstado = espacio.getEspacioEstado().get(espacio.getEspacioEstado().size() - 1);
                    return DTOResultadoBusquedaSEP.builder()
                            .idSEP(espacio.getId())
                            .nombreEspacio(espacio.getNombre())
                            .estado(espacioEstado.getEstadoEspacio().getNombre())
                            .fechaIngreso(espacio.getFechaHoraAlta() == null ? null
                                    : espacio.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .fechaUltimoCambioEstado(espacioEstado.getFechaHoraAlta() == null ? null
                                    : espacioEstado.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .build();
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sepDto.size());

        List<DTOResultadoBusquedaSEP> pageContent =
                start > sepDto.size()
                        ? Collections.emptyList()
                        : sepDto.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sepDto.size());
    }


    @Override
    public DTOEspacioPrivadoCompleto obtenerDetalleSolcitudEPrivado(Long idEspacio)throws Exception{
        Espacio espacio=this.espacioRepository.findById(idEspacio).orElseThrow(() -> new Exception("Espacio no encontrado"));
        DTOEspacioPrivadoCompleto dtoEspacioPrivado=DTOEspacioPrivadoCompleto.builder()
                .idEspacio(espacio.getId())
                .nombreEspacio(espacio.getNombre())
                .descripcion(espacio.getDescripcion())
                .direccion(espacio.getDireccionUbicacion())
                .longitud(espacio.getLongitudUbicacion().doubleValue())
                .latitud(espacio.getLatitudUbicacion().doubleValue())
                .fechaIngreso(espacio.getFechaHoraAlta() == null ? null
                        : espacio.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();

        EspacioEstado espacioEstadoActual = this.espacioEstadoRepository.findActualByEspacio(idEspacio);
        List<EstadoEspacio> estadosPosibles=this.estadoEspacioRepository.findDestinosByOrigen(espacioEstadoActual.getEstadoEspacio().getId());
        List<DTOEstadoEspacio> estadosTransicion=new ArrayList<>();
        for(EstadoEspacio estadoPosible:estadosPosibles){
            estadosTransicion.add(DTOEstadoEspacio.builder()
                    .id(estadoPosible.getId())
                    .nombre(estadoPosible.getNombre())
                    .descripcion(estadoPosible.getDescripcion())
                    .build());
        }

        estadosTransicion.add(DTOEstadoEspacio.builder()
                .id(espacioEstadoActual.getEstadoEspacio().getId())
                .nombre(espacioEstadoActual.getEstadoEspacio().getNombre())
                .descripcion(espacioEstadoActual.getEstadoEspacio().getDescripcion())
                .build());

        dtoEspacioPrivado.setEstadosPosibles(estadosTransicion);

        Usuario propietario= this.usuarioRepository.buscarUsuarioPropietarioEspacio(idEspacio);

        DTOEspacioPrivadoCompleto.Solicitante solicitante= DTOEspacioPrivadoCompleto.Solicitante.builder()
                .nombre(propietario.getNombre())
                .apellido(propietario.getApellido())
                .username(propietario.getUsername())
                .email(propietario.getMail())
                .build();

        if(propietario.getFotoPerfil()!=null){
            Path path = Paths.get(directorioPerfiles);
            String base64Image = encodeFileToBase64(path.resolve(propietario.getFotoPerfil()).toAbsolutePath().toString());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            solicitante.setContentType(contentType);
            solicitante.setUrlFotoPerfil(base64Data);
        } else {
            File file = new File(getClass().getResource("/default.png").getFile());
            Path path = file.toPath();

            String base64Image = encodeFileToBase64(path.toAbsolutePath().toString());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            solicitante.setContentType(contentType);
            solicitante.setUrlFotoPerfil(base64Data);
        }

        dtoEspacioPrivado.setSolicitante(solicitante);

        List<DTOArchivo> archivos = new ArrayList<>();
        for (DocumentacionEspacio documentacionEspacio : espacio.getDocumentacionEspacios()) {
            //byte[] content = Files.readAllBytes(Paths.get(directorioBase, documentacionEspacio.getDocumentacion()));
            DTOArchivo archivo = DTOArchivo.builder()
                    .id(documentacionEspacio.getId())
                    .nombreArchivo(documentacionEspacio.getDocumentacion())
                    //.base64(Base64.getEncoder().encodeToString(content))
                    .build();
            archivos.add(archivo);
        }

        dtoEspacioPrivado.setDocumentacion(archivos);

        List<EspacioEstado> espacioEstados=this.espacioEstadoRepository.findAllByEspacio(idEspacio);
        List<DTOEspacioPrivadoCompleto.EspacioEstado>dtoEPEstados=new ArrayList<>();
        for(EspacioEstado espacioEstado:espacioEstados){

            dtoEPEstados.add(DTOEspacioPrivadoCompleto.EspacioEstado.builder()
                    .id(espacioEstado.getId())
                    .nombre(espacioEstado.getEstadoEspacio().getNombre())
                    .descripcion(espacioEstado.getDescripcion())
                    .fechaHoraDesde(espacioEstado.getFechaHoraAlta() == null ? null
                            : espacioEstado.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .build());
        }

        dtoEspacioPrivado.setEspacioEstados(dtoEPEstados);

        return dtoEspacioPrivado;
    }

    @Override
    public byte[] generarDocumentacionZip(Long idEspacio) throws Exception {
        Espacio espacio = espacioRepository.findById(idEspacio)
                .orElseThrow(() -> new Exception("Espacio no encontrado"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            for (DocumentacionEspacio doc : espacio.getDocumentacionEspacios()) {
                Path filePath = Paths.get(directorioBase, doc.getDocumentacion());
                if (Files.exists(filePath)) {
                    ZipEntry zipEntry = new ZipEntry(doc.getDocumentacion());
                    zipOut.putNextEntry(zipEntry);
                    Files.copy(filePath, zipOut);
                    zipOut.closeEntry();
                }
            }
        }
        return baos.toByteArray();
    }

    @Override
    public void cambiarEstadoSEPrivado(DTOCambioEstadoSEP dtoCambioEstado, String username)throws Exception{
        Espacio espacio=this.espacioRepository.findById(dtoCambioEstado.getIdSEP()).orElseThrow(() -> new Exception("Espacio no encontrado"));
        EspacioEstado espacioEstadoActual =  this.espacioEstadoRepository.findActualByEspacio(dtoCambioEstado.getIdSEP());
        EstadoEspacio estadoEspacio=this.estadoEspacioRepository.findById(dtoCambioEstado.getIdEstado()).orElseThrow(() -> new Exception("Estado no encontrado"));

        espacioEstadoActual.setFechaHoraBaja(LocalDateTime.now());
        this.espacioEstadoRepository.save(espacioEstadoActual);
        EspacioEstado espacioEstado = EspacioEstado.builder()
                .estadoEspacio(estadoEspacio)
                .espacio(espacio)
                .fechaHoraAlta(LocalDateTime.now())
                .descripcion(dtoCambioEstado.getDescripcion())
                .build();
        espacioEstado=this.espacioEstadoRepository.save(espacioEstado);
        registroSingleton.write("Espacios", "espacio_privado", "creacion", "SEPEstado de ID " + estadoEspacio.getId());
        registroSingleton.write("Espacios", "espacio_privado", "eliminacion", "SEPEstado de ID " + espacioEstadoActual.getId());
    }

    //region de métodos auxiliares
    private static void validarDatosSolicitud(DTOCrearSolicitudEspacio dtoSolicitud) throws Exception {
        if (dtoSolicitud == null) throw new Exception("Datos de espacio requerido");
        if (dtoSolicitud.getNombre() == null || dtoSolicitud.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoSolicitud.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoSolicitud.getDireccion() == null || dtoSolicitud.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoSolicitud.getDireccion().length() > 50)
            throw new Exception("La dirección no debe superar 50 caracteres");
        if (dtoSolicitud.getDescripcion() != null && dtoSolicitud.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoSolicitud.getLatitud() == 0 || dtoSolicitud.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoSolicitud.getJustificacion() == null )
            throw new Exception("La justificación es obligatoria");
        if (!dtoSolicitud.getJustificacion().isEmpty() && dtoSolicitud.getJustificacion().length() < 50)
            throw new Exception("La justificación debe tener al menos 50 caracteres");
    }

    private String encodeFileToBase64(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

            String contentType;
            if (filePath.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else {
                contentType = "image/png";
            }

            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:" + contentType + ";base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo archivo de imagen: " + filePath, e);
        }
    }

}
