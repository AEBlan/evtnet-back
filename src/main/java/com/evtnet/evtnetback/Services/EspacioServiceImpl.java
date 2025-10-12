package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import com.evtnet.evtnetback.dto.espacios.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EspacioServiceImpl extends BaseServiceImpl<Espacio, Long> implements EspacioService {

    private final EspacioRepository espacioRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepository;
    private final SubEspacioRepository subEspacioRepository;
    private final AdministradorEspacioRepository administradorEspacioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoEspacioRepository tipoEspacioRepository;
    private final EstadoEspacioRepository estadoEspacioRepository;
    private final EspacioEstadoRepository espacioEstadoRepository;
    private final DocumentacionEspacioRepository documentacionEspacioRepository;
    private final TipoAdministradorEspacioRepository tipoAdministradorEspacioRepository;
    private final SolicitudEspacioPublicoRepository solicitudEspacioPublicoRepository;
    private final ParametroSistemaRepository parametroSistemaRepository;
    private final ImagenEspacioRepository imagenEspacioRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    @Value("${app.storage.documentacion:/app/storage/documentacion}")
    private String directorioBase;

    public EspacioServiceImpl(
            EspacioRepository espacioRepository,
            DisciplinaRepository disciplinaRepository,
            DisciplinaSubEspacioRepository disciplinaSubEspacioRepository,
            SubEspacioRepository subEspacioRepository,
            TipoEspacioRepository tipoEspacioRepository,
            AdministradorEspacioRepository administradorEspacioRepository,
            UsuarioRepository usuarioRepository,
            EstadoEspacioRepository estadoEspacioRepository,
            EspacioEstadoRepository espacioEstadoRepository,
            DocumentacionEspacioRepository documentacionEspacioRepository,
            TipoAdministradorEspacioRepository tipoAdministradorEspacioRepository,
            SolicitudEspacioPublicoRepository solicitudEspacioPublicoRepository,
            ParametroSistemaRepository parametroSistemaRepository,
            ImagenEspacioRepository imagenEspacioRepository,
            CaracteristicaRepository caracteristicaRepository
    ) {
        super(espacioRepository);
        this.espacioRepository = espacioRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaSubEspacioRepository = disciplinaSubEspacioRepository;
        this.subEspacioRepository = subEspacioRepository;
        this.tipoEspacioRepository = tipoEspacioRepository;
        this.administradorEspacioRepository = administradorEspacioRepository;
        this.usuarioRepository = usuarioRepository;
        this.estadoEspacioRepository = estadoEspacioRepository;
        this.espacioEstadoRepository = espacioEstadoRepository;
        this.documentacionEspacioRepository = documentacionEspacioRepository;
        this.tipoAdministradorEspacioRepository = tipoAdministradorEspacioRepository;
        this.solicitudEspacioPublicoRepository = solicitudEspacioPublicoRepository;
        this.parametroSistemaRepository = parametroSistemaRepository;
        this.imagenEspacioRepository = imagenEspacioRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    @Override
    public Long crearEspacio(DTOCrearEspacio dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception{
        validarDatosCreacion(dtoEspacio);

        String rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());

        TipoEspacio tipoEspacio;
        if(dtoEspacio.isEsPublico()){
            tipoEspacio = this.tipoEspacioRepository.findByNombre("Público").get();
        }else{
            tipoEspacio = this.tipoEspacioRepository.findByNombre("Privado").get();
        }

        Espacio espacio = Espacio.builder()
                .nombre(dtoEspacio.getNombre())
                .descripcion(dtoEspacio.getDescripcion())
                .direccionUbicacion(dtoEspacio.getDireccion())
                .latitudUbicacion(new BigDecimal(dtoEspacio.getLatitud()))
                .longitudUbicacion(new BigDecimal(dtoEspacio.getLongitud()))
                .fechaHoraAlta(LocalDateTime.now())
                .basesYCondiciones(rutaBasesYCondiciones)
                .tipoEspacio(tipoEspacio)
                .build();

        SolicitudEspacioPublico solicitud;
        if(dtoEspacio.getSepId()!=null && dtoEspacio.getSepId()>0 ){
            solicitud=this.solicitudEspacioPublicoRepository.findById(dtoEspacio.getSepId()).get();
            espacio.setSolicitudEspacioPublico(solicitud);
        }

        espacio = save(espacio);

        AdministradorEspacio propietario = AdministradorEspacio.builder()
                .espacio(espacio)
                .usuario(this.usuarioRepository.findByUsername(dtoEspacio.getUsername()).get())
                .tipoAdministradorEspacio(this.tipoAdministradorEspacioRepository.findByNombre("Propietario").get())
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        this.administradorEspacioRepository.save(propietario);

        EspacioEstado espacioEstado = EspacioEstado.builder()
                .estadoEspacio(this.estadoEspacioRepository.findByNombre("En_revisión").get())
                .espacio(espacio)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        this.espacioEstadoRepository.save(espacioEstado);

        if (documentacion != null) {
            for (MultipartFile doc : documentacion) {
                String rutaDoc = guardarArchivo(doc, dtoEspacio.getNombre());
                DocumentacionEspacio documentacionEspacio = DocumentacionEspacio.builder()
                        .espacio(espacio)
                        .documentacion(rutaDoc)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
                this.documentacionEspacioRepository.save(documentacionEspacio);
            }
        }

        for (DTOSubespacio dtoSubEspacio : dtoEspacio.getSubEspacios()){
            SubEspacio subEspacio = SubEspacio.builder()
                    .nombre(dtoSubEspacio.getNombre())
                    .descripcion(dtoSubEspacio.getDescripcion())
                    .capacidadmaxima(dtoSubEspacio.getCapacidadMaxima())
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            if (!dtoSubEspacio.getDisciplinas().isEmpty()) {
                List<DisciplinaSubEspacio> disciplinas = new ArrayList<>();
                for (Long disciplinaID : dtoSubEspacio.getDisciplinas()) {
                    disciplinas.add(DisciplinaSubEspacio.builder().disciplina(this.disciplinaRepository.findById(disciplinaID).get()).build());
                }
                subEspacio.setDisciplinasSubespacio(disciplinas);
            }
            this.subEspacioRepository.save(subEspacio);
        }

        return espacio.getId();
    }

    @Override
    public DTOEspacio obtenerEspacio(Long id, String username) throws Exception{
        Espacio espacio = espacioRepository.findById(id).get();
        TipoEspacio tipoEspacio = this.tipoEspacioRepository.findById(espacio.getTipoEspacio().getId()).get();
        List<ImagenEspacio> imagenEspacio = this.imagenEspacioRepository.findByEspacio_IdOrderByOrdenAsc(id);
        List<SubEspacio> subspacios = this.subEspacioRepository.findAllByEspacio(id);
        List<DTOSubespacioDetalle> dtoSubespacios = new ArrayList<>();

        for (SubEspacio subEspacio : subspacios) {
            dtoSubespacios.add(DTOSubespacioDetalle.builder()
                    .nombre(subEspacio.getNombre())
                    .descripcion(subEspacio.getDescripcion())
                    .capacidadMaxima(subEspacio.getCapacidadmaxima())
                    .disciplinas(this.disciplinaSubEspacioRepository.disciplinasNombre(subEspacio.getId()))
                    .caracteristicas(this.caracteristicaRepository.caracteristicas(subEspacio.getId()))
                    .build());

        }

        return DTOEspacio.builder()
                .nombre(espacio.getNombre())
                .tipoEspacio(tipoEspacio.getNombre())
                .descripcion(espacio.getDescripcion())
                .direccion(espacio.getDireccionUbicacion())
                .latitud(espacio.getLatitudUbicacion().doubleValue())
                .longitud(espacio.getLongitudUbicacion().doubleValue())
                .cantidadImagenes(imagenEspacio.size())
                .subEspacios(dtoSubespacios)
                .esAdmin(this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Administrador"))
                .build();
    }

    @Override
    public DTOEspacioEditar obtenerEspacioEditar(Long id, String username) throws Exception{
        Espacio espacio = espacioRepository.findById(id).get();
        TipoEspacio tipoEspacio = this.tipoEspacioRepository.findById(espacio.getTipoEspacio().getId()).get();
        List<SubEspacio> subspacios = this.subEspacioRepository.findAllByEspacio(id);
        List<DTOSubespacioEditar> dtoSubespacios = new ArrayList<>();

        for (SubEspacio subEspacio : subspacios) {
            dtoSubespacios.add(DTOSubespacioEditar.builder()
                            .id(subEspacio.getId())
                            .nombre(subEspacio.getNombre())
                            .descripcion(subEspacio.getDescripcion())
                            .capacidadMaxima(subEspacio.getCapacidadmaxima())
                            .disciplinas(this.disciplinaSubEspacioRepository.findAllBySubespacio(subEspacio.getId()))
                            .build());

        }
        boolean esAdmin=this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Administrador");
        DTOEspacioEstado espacioEstado = this.estadoEspacioRepository.espacioEstadoByEspacio(id);

        List<DTOArchivo> archivos = new ArrayList<>();
        for (DocumentacionEspacio documentacionEspacio : espacio.getDocumentacionEspacios()) {
            byte[] content = Files.readAllBytes(Paths.get("/app/storage/documentacion", documentacionEspacio.getDocumentacion()));
            DTOArchivo archivo = DTOArchivo.builder()
                    .nombreArchivo(documentacionEspacio.getDocumentacion())
                    .base64(Base64.getEncoder().encodeToString(content))
                    .build();
            archivos.add(archivo);
        }

        byte[] content = Files.readAllBytes(Paths.get("/app/storage/documentacion", espacio.getBasesYCondiciones()));
        DTOArchivo archivo = DTOArchivo.builder()
                .nombreArchivo(espacio.getBasesYCondiciones())
                .base64(Base64.getEncoder().encodeToString(content))
                .build();

        return DTOEspacioEditar.builder()
                .id(id)
                .nombre(espacio.getNombre())
                .descripcion(espacio.getDescripcion())
                .direccion(espacio.getDireccionUbicacion())
                .latitud(espacio.getLatitudUbicacion().doubleValue())
                .longitud(espacio.getLongitudUbicacion().doubleValue())
                .subEspacios(dtoSubespacios)
                .esAdmin(esAdmin)
                .esPropietario(!esAdmin)
                .esPublico(tipoEspacio.getNombre().equalsIgnoreCase("público"))
                .estado(espacioEstado)
                .documentacion(archivos)
                .basesYCondiciones(archivo)
                .build();
    }

    @Override
    public void editarEspacio(DTOEspacioEditar dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception{
        Espacio espacio = this.espacioRepository.findById(dtoEspacio.getId())
                .orElseThrow(() -> new Exception("Espacio no encontrado"));

        validarDatosEdicion(dtoEspacio);

        String rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());

        espacio.setNombre(dtoEspacio.getNombre());
        espacio.setDescripcion(dtoEspacio.getDescripcion());
        espacio.setDireccionUbicacion(dtoEspacio.getDireccion());
        espacio.setLatitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLatitud()));
        espacio.setLongitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLongitud()));
        espacio.setBasesYCondiciones(rutaBasesYCondiciones);

        espacio = update(dtoEspacio.getId(),espacio);

        EspacioEstado espacioEstadoActual =  this.espacioEstadoRepository.findActualByEspacio(dtoEspacio.getId());

        if(!espacioEstadoActual.getEstadoEspacio().getNombre().equalsIgnoreCase(dtoEspacio.getEstado().getNombre())){
            espacioEstadoActual.setFechaHoraBaja(LocalDateTime.now());
            this.espacioEstadoRepository.save(espacioEstadoActual);
            EspacioEstado espacioEstado = EspacioEstado.builder()
                    .estadoEspacio(this.estadoEspacioRepository.findByNombre(dtoEspacio.getEstado().getNombre()).get())
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            this.espacioEstadoRepository.save(espacioEstado);
        }

        // Obtener los documentos existentes del espacio
        List<DocumentacionEspacio> docsExistentes = this.documentacionEspacioRepository.findByEspacioId(espacio.getId());

        // Crear una lista con los nombres de los documentos enviados desde el front (DTOArchivo)
        List<String> nombresDocsEnviados = dtoEspacio.getDocumentacion() != null
                ? dtoEspacio.getDocumentacion().stream().map(DTOArchivo::getNombreArchivo).toList()
                : new ArrayList<>();

        // Eliminar documentos que ya no están en la lista enviada
        for (DocumentacionEspacio doc : docsExistentes) {
            if (!nombresDocsEnviados.contains(doc.getDocumentacion())) {
                this.documentacionEspacioRepository.delete(doc);
            }
        }

        if (documentacion != null) {
            for (MultipartFile doc : documentacion) {
                String rutaDoc = guardarArchivo(doc, dtoEspacio.getNombre());
                DocumentacionEspacio documentacionEspacio = DocumentacionEspacio.builder()
                        .espacio(espacio)
                        .documentacion(rutaDoc)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
                this.documentacionEspacioRepository.save(documentacionEspacio);
            }
        }

        // Obtener subespacios existentes
        List<SubEspacio> subEspaciosExistentes = this.subEspacioRepository.findAllByEspacio(espacio.getId());

        // Lista de IDs enviados desde el front
        List<Long> idsSubespaciosEnviados = dtoEspacio.getSubEspacios().stream()
                .map(DTOSubespacioEditar::getId)
                .filter(Objects::nonNull)
                .toList();

        // Eliminar subespacios que no están en la lista enviada
        for (SubEspacio sub : subEspaciosExistentes) {
            if (!idsSubespaciosEnviados.contains(sub.getId())) {
                this.subEspacioRepository.delete(sub);
            }
        }

        for (DTOSubespacioEditar dtoSub : dtoEspacio.getSubEspacios()) {
            SubEspacio subEspacio;
            if (dtoSub.getId() != null) {
                // Subespacio existente
                subEspacio = this.subEspacioRepository.findById(dtoSub.getId())
                        .orElseThrow(() -> new Exception("SubEspacio no encontrado: " + dtoSub.getNombre()));
            } else {
                // Subespacio nuevo
                subEspacio = SubEspacio.builder()
                        .espacio(espacio)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
            }

            // Actualizamos los datos básicos
            subEspacio.setNombre(dtoSub.getNombre());
            subEspacio.setDescripcion(dtoSub.getDescripcion());
            subEspacio.setCapacidadmaxima(dtoSub.getCapacidadMaxima());

            // --------- Actualización de disciplinas ---------
            // Traer disciplinas actuales
            List<DisciplinaSubEspacio> disciplinasExistentes = subEspacio.getDisciplinasSubespacio();

            // IDs de disciplinas enviadas por el front
            List<Long> idsEnviadas = dtoSub.getDisciplinas() != null
                    ? dtoSub.getDisciplinas().stream().map(DTODisciplinas::getId).toList()
                    : new ArrayList<>();

            // Eliminar las que ya no están
            disciplinasExistentes.removeIf(dse -> !idsEnviadas.contains(dse.getDisciplina().getId()));

            // Agregar nuevas disciplinas
            for (DTODisciplinas dtoDisc : dtoSub.getDisciplinas()) {
                boolean existe = disciplinasExistentes.stream()
                        .anyMatch(dse -> dse.getDisciplina().getId().equals(dtoDisc.getId()));
                if (!existe) {
                    disciplinasExistentes.add(DisciplinaSubEspacio.builder()
                            .disciplina(this.disciplinaRepository.findById(dtoDisc.getId())
                                    .orElseThrow(() -> new Exception("Disciplina no encontrada: " + dtoDisc.getId())))
                            .build());
                }
            }

            subEspacio.setDisciplinasSubespacio(disciplinasExistentes);
            this.subEspacioRepository.save(subEspacio);
        }


    }

    @Override
    public void dejarDeAdministrar(Long id, String username)throws Exception{
        AdministradorEspacio administradorEspacio = this.administradorEspacioRepository.findByEspacioAndUser(id, username);
        administradorEspacio.setFechaHoraBaja(LocalDateTime.now());
        this.administradorEspacioRepository.save(administradorEspacio);
    }

    @Override
    public String obtenerNombreEspacio(Long id)throws Exception{
        Espacio espacio = this.espacioRepository.findById(id)
                .orElseThrow(() -> new Exception("Espacio no encontrado"));
        return espacio.getNombre();
    }

    @Override
    public List<DTOTipoEspacio> obtenerTiposEspacio()throws Exception{
        List<TipoEspacio> tiposEspacio = this.tipoEspacioRepository.findAll();
        List<DTOTipoEspacio> dtoTiposEspacio = new ArrayList<>();
        for (TipoEspacio tipoEspacio : tiposEspacio) {
            dtoTiposEspacio.add(DTOTipoEspacio.builder()
                            .id(tipoEspacio.getId())
                            .nombre(tipoEspacio.getNombre())
                            .build());
        }
        return dtoTiposEspacio;
    }

    @Override
    public List<DTOResultadoBusquedaEspacios> buscarEspacios(DTOBusquedaEspacios dtoEspacio)throws Exception{
        double rangoMetros = dtoEspacio.getUbicacion().getRango();

        // Conversión: 1° de latitud ≈ 111.320 metros
        double gradosPorMetro = 1.0 / 111_320.0;

        double rangoGrados = rangoMetros * gradosPorMetro;

        double latDesde = dtoEspacio.getUbicacion().getLatitud() - rangoGrados;
        double latHasta = dtoEspacio.getUbicacion().getLatitud() + rangoGrados;
        double lonDesde = dtoEspacio.getUbicacion().getLongitud() - rangoGrados;
        double lonHasta = dtoEspacio.getUbicacion().getLongitud() + rangoGrados;

        String[] palabras = dtoEspacio.getTexto().split(" ");
        List<Espacio> espacios = new ArrayList<>();
        for (String palabra : palabras){
            if(palabra.length()>2){
                espacios.addAll(espacioRepository.findEspacios(palabra, dtoEspacio.getTipos(), new BigDecimal(latDesde), new BigDecimal(latHasta), new BigDecimal(lonDesde), new BigDecimal(lonHasta)));
            }
        }
        // Eliminar duplicados por ID
        List<Espacio> espaciosUnicos = espacios.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Espacio::getId, e -> e, (e1, e2) -> e1),
                        m -> new ArrayList<>(m.values())
                ));
        List<DTOResultadoBusquedaEspacios> espaciosDto=new ArrayList<>();
        for (Espacio espacio:espaciosUnicos){
            Set<String> disciplinasSet = new HashSet<>();

            for (SubEspacio subEspacio : espacio.getSubEspacios()) {
                disciplinasSet.addAll(this.disciplinaSubEspacioRepository.disciplinasNombre(subEspacio.getId()));
            }
            espaciosDto.add(DTOResultadoBusquedaEspacios.builder()
                            .id(espacio.getId())
                            .nombre(espacio.getNombre())
                            .tipo(espacio.getTipoEspacio().getNombre())
                            .disciplinas(new ArrayList<>(disciplinasSet))
                    .build());
        }

        return espaciosDto;
    }

    @Override
    public List<DTOResultadoBusquedaMisEspacios> buscarMisEspacios(DTOBusquedaMisEspacios dtoEspacio)throws Exception{
        String[] palabras = dtoEspacio.getTexto().split(" ");
        List<Espacio> espacios = new ArrayList<>();
        for (String palabra : palabras){
            espacios.addAll(espacioRepository.findMisEspacios(palabra, dtoEspacio.isAdministrador(), dtoEspacio.isPropietario(), dtoEspacio.getUsername()));
        }
        // Eliminar duplicados por ID
        List<Espacio> espaciosUnicos = espacios.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Espacio::getId, e -> e, (e1, e2) -> e1),
                        m -> new ArrayList<>(m.values())
                ));
        List<DTOResultadoBusquedaMisEspacios> espaciosDto=new ArrayList<>();
        for (Espacio espacio:espaciosUnicos){
            Set<String> disciplinasSet = new HashSet<>();

            for (SubEspacio subEspacio : espacio.getSubEspacios()) {
                disciplinasSet.addAll(this.disciplinaSubEspacioRepository.disciplinasNombre(subEspacio.getId()));
            }
            espaciosDto.add(DTOResultadoBusquedaMisEspacios.builder()
                    .id(espacio.getId())
                    .nombre(espacio.getNombre())
                    .rol(this.espacioRepository.rolByEspacioUsername(espacio.getId(), dtoEspacio.getUsername()))
                    .disciplinas(new ArrayList<>(disciplinasSet))
                    .build());
        }

        return espaciosDto;
    }

    //Región de métodos auxiliares
    private void validarDatosCreacion(DTOCrearEspacio dtoEspacio) throws Exception{
        if (dtoEspacio == null) throw new Exception("Payload requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50) throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank()) throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 50) throw new Exception("La dirección no debe superar 50 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500) throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0) throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoEspacio.getSubEspacios().isEmpty()) throw new Exception("Debe agregar al menos un subespacio");

        ParametroSistema parametroRangoUbicacion = this.parametroSistemaRepository.findByNombre("rango_validar_ubicacion");

        double rangoMetros = Double.parseDouble(parametroRangoUbicacion.getValor());

        // Conversión: 1° de latitud ≈ 111.320 metros
        double gradosPorMetro = 1.0 / 111_320.0;

        double rangoGrados = rangoMetros * gradosPorMetro;

        double latDesde = dtoEspacio.getLatitud() - rangoGrados;
        double latHasta = dtoEspacio.getLatitud() + rangoGrados;
        double lonDesde = dtoEspacio.getLongitud() - rangoGrados;
        double lonHasta = dtoEspacio.getLongitud() + rangoGrados;

        Long otrosEspacios = this.espacioRepository.findDuplicado(dtoEspacio.getNombre(), dtoEspacio.getUsername(), new BigDecimal(latDesde), new BigDecimal(latHasta), new BigDecimal(lonDesde), new BigDecimal(lonHasta));
        if(otrosEspacios>0) throw new Exception("Ya existe otro espacio con estos datos");

        Set<String> nombres = new HashSet<>();

        for (var subEspacio : dtoEspacio.getSubEspacios()) {
            String nombreNormalizado = subEspacio.getNombre().trim().toLowerCase();
            if (!nombres.add(nombreNormalizado)) {
                throw new Exception("No puede haber dos subespacios con el mismo nombre: " + subEspacio.getNombre());
            }
        }
    }

    private void validarDatosEdicion(DTOEspacioEditar dtoEspacio) throws Exception{
        if (dtoEspacio == null) throw new Exception("Payload requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank()) throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50) throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank()) throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 50) throw new Exception("La dirección no debe superar 50 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500) throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0) throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoEspacio.getSubEspacios().isEmpty()) throw new Exception("Debe agregar al menos un subespacio");

        ParametroSistema parametroRangoUbicacion = this.parametroSistemaRepository.findByNombre("rango_validar_ubicacion");

        double rangoMetros = Double.parseDouble(parametroRangoUbicacion.getValor());

        // Conversión: 1° de latitud ≈ 111.320 metros
        double gradosPorMetro = 1.0 / 111_320.0;

        double rangoGrados = rangoMetros * gradosPorMetro;

        double latDesde = dtoEspacio.getLatitud() - rangoGrados;
        double latHasta = dtoEspacio.getLatitud() + rangoGrados;
        double lonDesde = dtoEspacio.getLongitud() - rangoGrados;
        double lonHasta = dtoEspacio.getLongitud() + rangoGrados;

        Long otrosEspacios = this.espacioRepository.findDuplicado(dtoEspacio.getNombre(), dtoEspacio.getUsername(), new BigDecimal(latDesde), new BigDecimal(latHasta), new BigDecimal(lonDesde), new BigDecimal(lonHasta));
        if(otrosEspacios>0) throw new Exception("Ya existe otro espacio con estos datos");

        Set<String> nombres = new HashSet<>();

        for (var subEspacio : dtoEspacio.getSubEspacios()) {
            String nombreNormalizado = subEspacio.getNombre().trim().toLowerCase();
            if (!nombres.add(nombreNormalizado)) {
                throw new Exception("No puede haber dos subespacios con el mismo nombre: " + subEspacio.getNombre());
            }
        }
    }

    public String guardarArchivo(MultipartFile archivo, String nombreEspacio) throws IOException {
//        File directorio = new File(directorioBase);
//        if (!directorio.exists()) {
//            directorio.mkdirs();
//        }

        if (!Files.exists(Paths.get(directorioBase))) {
            Files.createDirectories(Paths.get(directorioBase));
        }

        String nombreArchivo = nombreEspacio + "_" + archivo.getOriginalFilename();
        //Path rutaDestino = Path.of(directorioBase, nombreArchivo);
        Path filePath=Paths.get(directorioBase).resolve(nombreArchivo).toAbsolutePath().normalize();
        Files.write(filePath, archivo.getBytes());

        //archivo.transferTo(rutaDestino.toFile());

        return filePath.toString();
    }
}
