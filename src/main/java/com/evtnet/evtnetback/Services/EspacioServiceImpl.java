package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import com.evtnet.evtnetback.dto.espacios.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    private final EventoRepository eventoRepository;
    private final EncargadoSubEspacioRepository encargadoSubEspacioRepository;

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
            CaracteristicaRepository caracteristicaRepository,
            EventoRepository eventoRepository,
            EncargadoSubEspacioRepository encargadoSubEspacioRepository
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
        this.eventoRepository  = eventoRepository;
        this.encargadoSubEspacioRepository = encargadoSubEspacioRepository;
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
                .requiereAprobarEventos(dtoEspacio.isRequiereAprobarEventos())
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
            subEspacio=this.subEspacioRepository.save(subEspacio);
            if (!dtoSubEspacio.getDisciplinas().isEmpty()) {
                List<DisciplinaSubEspacio> disciplinas = new ArrayList<>();
                for (Long disciplinaID : dtoSubEspacio.getDisciplinas()) {
                    this.disciplinaSubEspacioRepository.save(DisciplinaSubEspacio.builder()
                            .disciplina(this.disciplinaRepository.findById(disciplinaID).get())
                            .subEspacio(subEspacio)
                            .build());
                }
            }

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
        boolean esAdmin=this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Administrador");
        if(!esAdmin) esAdmin=this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Propietario");

        EspacioEstado espacioEstado=this.espacioEstadoRepository.findActualByEspacio(id);

        return DTOEspacio.builder()
                .nombre(espacio.getNombre())
                .tipoEspacio(tipoEspacio.getNombre())
                .descripcion(espacio.getDescripcion())
                .direccion(espacio.getDireccionUbicacion())
                .latitud(espacio.getLatitudUbicacion().doubleValue())
                .longitud(espacio.getLongitudUbicacion().doubleValue())
                .cantidadImagenes(imagenEspacio.size())
                .subEspacios(dtoSubespacios)
                .esAdmin(esAdmin)
                .estado(DTOEspacioEstado.builder()
                        .id(espacioEstado.getEstadoEspacio().getId())
                        .nombre(espacioEstado.getEstadoEspacio().getNombre())
                        .descripcion(espacioEstado.getDescripcion())
                        .build())
                .build();
    }

    @Override
    public DTOEspacioEditar obtenerEspacioEditar(Long id, String username) throws Exception{
        Espacio espacio = espacioRepository.findById(id).get();
        TipoEspacio tipoEspacio = this.tipoEspacioRepository.findById(espacio.getTipoEspacio().getId()).get();
        List<SubEspacio> subspacios = this.subEspacioRepository.findAllByEspacio(id);
        List<DTOSubespacioEditar> dtoSubespacios = new ArrayList<>();

        for (SubEspacio subEspacio : subspacios) {
            DTOSubespacioEditar dtoSubespacioEditar=DTOSubespacioEditar.builder()
                    .id(subEspacio.getId())
                    .nombre(subEspacio.getNombre())
                    .descripcion(subEspacio.getDescripcion())
                    .capacidadMaxima(subEspacio.getCapacidadmaxima())
                    .build();


            List<DisciplinaSubEspacio> disciplinasSubEspacio=this.disciplinaSubEspacioRepository.findAllBySubespacio(subEspacio.getId());
            List<DTODisciplinas>disciplinas=new ArrayList<>();
            for (DisciplinaSubEspacio disciplina:disciplinasSubEspacio) {
                disciplinas.add(DTODisciplinas.builder()
                                .id(disciplina.getDisciplina().getId())
                                .nombre(disciplina.getDisciplina().getNombre())
                        .build());
            }
            dtoSubespacioEditar.setDisciplinas(disciplinas);
            dtoSubespacios.add(dtoSubespacioEditar);
        }
        boolean esAdmin=this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Administrador");
        EspacioEstado espacioEstado = this.espacioEstadoRepository.findActualByEspacio(id);
        List<EstadoEspacio> estadosPosibles=this.estadoEspacioRepository.findDestinosByOrigen(espacioEstado.getEstadoEspacio().getId());
        List<DTOEstadoEspacio> estadosTransicion=new ArrayList<>();
        for(EstadoEspacio estadoPosible:estadosPosibles){
            estadosTransicion.add(DTOEstadoEspacio.builder()
                            .id(estadoPosible.getId())
                            .nombre(estadoPosible.getNombre())
                            .descripcion(estadoPosible.getDescripcion())
                    .build());
        }

        estadosTransicion.add(DTOEstadoEspacio.builder()
                .id(espacioEstado.getEstadoEspacio().getId())
                .nombre(espacioEstado.getEstadoEspacio().getNombre())
                .descripcion(espacioEstado.getEstadoEspacio().getDescripcion())
                .build());

        List<DTOArchivo> archivos = new ArrayList<>();
        for (DocumentacionEspacio documentacionEspacio : espacio.getDocumentacionEspacios()) {
            byte[] content = Files.readAllBytes(Paths.get(directorioBase, documentacionEspacio.getDocumentacion()));
            DTOArchivo archivo = DTOArchivo.builder()
                    .id(documentacionEspacio.getId())
                    .nombreArchivo(documentacionEspacio.getDocumentacion())
                    .base64(Base64.getEncoder().encodeToString(content))
                    .build();
            archivos.add(archivo);
        }

        byte[] content = Files.readAllBytes(Paths.get(directorioBase, espacio.getBasesYCondiciones()));
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
                .estado(DTOEspacioEstado.builder()
                        .id(espacioEstado.getEstadoEspacio().getId())
                        .nombre(espacioEstado.getEstadoEspacio().getNombre())
                        .descripcion(espacioEstado.getDescripcion())
                        .build())
                .documentacion(archivos)
                .basesYCondiciones(archivo)
                .estadosEspacio(estadosTransicion)
                .requiereAprobacion(espacio.getRequiereAprobarEventos())
                .build();
    }

    @Override
    public void editarEspacio(DTOEspacioEditar dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception{
        Espacio espacio = this.espacioRepository.findById(dtoEspacio.getId())
                .orElseThrow(() -> new Exception("Espacio no encontrado"));

        validarDatosEdicion(dtoEspacio);

        if (basesYCondiciones != null && !basesYCondiciones.isEmpty()) {
            // Se subió un nuevo archivo → reemplazo
            String rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());
            espacio.setBasesYCondiciones(rutaBasesYCondiciones);
        }
        //String rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());

        espacio.setNombre(dtoEspacio.getNombre());
        espacio.setDescripcion(dtoEspacio.getDescripcion());
        espacio.setDireccionUbicacion(dtoEspacio.getDireccion());
        espacio.setLatitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLatitud()));
        espacio.setLongitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLongitud()));

        espacio = update(dtoEspacio.getId(),espacio);

        EspacioEstado espacioEstadoActual =  this.espacioEstadoRepository.findActualByEspacio(dtoEspacio.getId());

        if(espacioEstadoActual.getEstadoEspacio().getId()!=dtoEspacio.getEstado().getId()){
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

        List<Long> idsDocsMantener = dtoEspacio.getDocumentacion() != null
                ? dtoEspacio.getDocumentacion().stream()
                .map(DTOArchivo::getId)
                .filter(Objects::nonNull)
                .toList()
                : new ArrayList<>();

        // Eliminar documentos que ya no están en la lista enviada
        for (DocumentacionEspacio doc : docsExistentes) {
            if (!idsDocsMantener.contains(doc.getId())) {
                documentacionEspacioRepository.delete(doc);
                eliminarArchivoFisico(doc.getDocumentacion());
            }
        }

        if (documentacion != null && !documentacion.isEmpty()) {
            for (MultipartFile file : documentacion) {
                if (file != null && !file.isEmpty()) {
                    String ruta = guardarArchivo(file, dtoEspacio.getNombre());
                    DocumentacionEspacio nuevaDoc = DocumentacionEspacio.builder()
                            .espacio(espacio)
                            .documentacion(ruta)
                            .fechaHoraAlta(LocalDateTime.now())
                            .build();
                    documentacionEspacioRepository.save(nuevaDoc);
                }
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

            if (disciplinasExistentes == null) {
                disciplinasExistentes = new ArrayList<>();
            }

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
    public List<DTOResultadoBusquedaEspacios> buscarEspacios(DTOBusquedaEspacios dtoEspacio) throws Exception {
        Set<Espacio> resultadoFinal = new HashSet<>();
        List<Set<Espacio>> setsPorFiltro = new ArrayList<>();

        // --- Filtro por ubicación ---
        if (dtoEspacio.getUbicacion() != null) {
            double rangoMetros = dtoEspacio.getUbicacion().getRango();
            double gradosPorMetro = 1.0 / 111_320.0;
            double rangoGrados = rangoMetros * gradosPorMetro;

            double latDesde = dtoEspacio.getUbicacion().getLatitud() - rangoGrados;
            double latHasta = dtoEspacio.getUbicacion().getLatitud() + rangoGrados;
            double lonDesde = dtoEspacio.getUbicacion().getLongitud() - rangoGrados;
            double lonHasta = dtoEspacio.getUbicacion().getLongitud() + rangoGrados;

            setsPorFiltro.add(new HashSet<>(
                    espacioRepository.findEspaciosByUbicacion(
                            new BigDecimal(latDesde),
                            new BigDecimal(latHasta),
                            new BigDecimal(lonDesde),
                            new BigDecimal(lonHasta)
                    )
            ));
        }

        // --- Filtro por texto ---
        if (dtoEspacio.getTexto() != null && !dtoEspacio.getTexto().isBlank()) {
            String[] palabras = dtoEspacio.getTexto().split(" ");
            Set<Espacio> porTexto = new HashSet<>();

            for (String palabra : palabras) {
                if (palabra.length() > 2) {
                    porTexto.addAll(espacioRepository.findEspaciosByTexto(palabra));
                }
            }
            setsPorFiltro.add(porTexto);
        }

        // --- Filtro por tipo ---
        if (dtoEspacio.getTipos() != null && !dtoEspacio.getTipos().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(espacioRepository.findEspaciosByTipo(dtoEspacio.getTipos())));
        }

        // --- Filtro por disciplina ---
        if (dtoEspacio.getDisciplinas() != null && !dtoEspacio.getDisciplinas().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(espacioRepository.findEspaciosByDisciplina(dtoEspacio.getDisciplinas())));
        }

        // --- Sin filtros: traer todos los habilitados ---
        if (setsPorFiltro.isEmpty()) {
            resultadoFinal.addAll(espacioRepository.findAllHabilitados());
        } else {
            // Iniciar con el primer conjunto y hacer intersección con los demás
            resultadoFinal.addAll(setsPorFiltro.get(0));

            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i)); // 🔹 Intersección de Sets
            }
        }

        // --- Mapear a DTO ---
        List<DTOResultadoBusquedaEspacios> espaciosDto = new ArrayList<>();
        for (Espacio espacio : resultadoFinal) {
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
    public List<DTOResultadoBusquedaMisEspacios> buscarMisEspacios(DTOBusquedaMisEspacios dtoEspacio, String username)throws Exception  {
        List<Espacio> espacios = new ArrayList<>();
        if(dtoEspacio.getTexto() != null && !dtoEspacio.getTexto().isBlank()){
            String[] palabras = dtoEspacio.getTexto().split(" ");

            for (String palabra : palabras){
                if(palabra.length()>2) {
                    espacios.addAll(espacioRepository.findMisEspacios(palabra, dtoEspacio.isAdministrador(), dtoEspacio.isPropietario(), username));
                }
            }
        }else{
            espacios.addAll(espacioRepository.findMisEspacios(dtoEspacio.getTexto(), dtoEspacio.isAdministrador(), dtoEspacio.isPropietario(), username));
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
                    .rol(this.espacioRepository.rolByEspacioUsername(espacio.getId(), username))
                    .disciplinas(new ArrayList<>(disciplinasSet))
                    .build());
        }

        return espaciosDto;
    }

    @Override
    public List<DTOResultadoBusquedaEventosPorEspacio> buscarEventosPorEspacio(DTOBusquedaEventosPorEspacio dto) throws Exception {
        Set<DTOEvento> resultadoFinal = new HashSet<>();
        List<Set<DTOEvento>> setsPorFiltro = new ArrayList<>();

        // --- Filtro por texto ---
        if (dto.getTexto() != null && !dto.getTexto().isBlank()) {
            String[] palabras = dto.getTexto().split(" ");
            Set<DTOEvento> porTexto = new HashSet<>();
            for (String palabra : palabras) {
                if (palabra.length() > 2) {
                    porTexto.addAll(eventoRepository.findEventosByTexto(dto.getIdEspacio(), palabra));
                }
            }
            setsPorFiltro.add(porTexto);
        }

        // --- Filtro por fechas ---
        if ((dto.getFechaDesde() != null || dto.getFechaHasta() != null) && (dto.getHoraDesde() == null && dto.getHoraHasta() == null)) {
            LocalDate fechaDesde = dto.getFechaDesde() != null
                    ? Instant.ofEpochMilli(dto.getFechaDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;
            LocalDate fechaHasta = dto.getFechaHasta() != null
                    ? Instant.ofEpochMilli(dto.getFechaHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByFecha(dto.getIdEspacio(), fechaDesde, fechaHasta)));
        }

        if((dto.getFechaDesde() == null && dto.getFechaHasta() == null) && (dto.getHoraDesde() != null || dto.getHoraHasta() != null)){
            LocalTime horaDesde = Instant.ofEpochMilli(dto.getHoraDesde()).atZone(ZoneId.systemDefault()).toLocalTime();
            LocalTime horaHasta = Instant.ofEpochMilli(dto.getHoraHasta()).atZone(ZoneId.systemDefault()).toLocalTime();

            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByHora(dto.getIdEspacio(), horaDesde, horaHasta)));
        }

        // --- Filtro por fechas y horas ---
        if (dto.getFechaDesde() != null && dto.getFechaHasta() != null && (dto.getHoraDesde() != null && dto.getHoraHasta() != null)) {

            LocalDate fechaDesde = dto.getFechaDesde() != null
                    ? Instant.ofEpochMilli(dto.getFechaDesde()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MIN;
            LocalDate fechaHasta = dto.getFechaHasta() != null
                    ? Instant.ofEpochMilli(dto.getFechaHasta()).atZone(ZoneId.systemDefault()).toLocalDate()
                    : LocalDate.MAX;

            LocalTime horaDesde = dto.getHoraDesde() != null
                    ? Instant.ofEpochMilli(dto.getHoraDesde())
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    : LocalTime.MIN;

            LocalTime horaHasta = dto.getHoraHasta() != null
                    ? Instant.ofEpochMilli(dto.getHoraHasta())
                    .atZone(ZoneId.systemDefault())
                    .toLocalTime()
                    : LocalTime.MAX;

            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByFechaYHora(dto.getIdEspacio(), fechaDesde, fechaHasta, horaDesde, horaHasta)));

        }

        // --- Filtro por precio ---
        if (dto.getPrecioLimite() >0) {
            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByPrecio(dto.getIdEspacio(), new BigDecimal(dto.getPrecioLimite()))));
        }

        // --- Filtro por disciplinas ---
        if (dto.getDisciplinas() != null && !dto.getDisciplinas().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByDisciplinas(dto.getIdEspacio(), dto.getDisciplinas())));
        }


        // --- Sin filtros: traer todos los eventos del espacio ---
        if (setsPorFiltro.isEmpty()) {
            //List<DTOEvento>eventos=new ArrayList<>();
            resultadoFinal.addAll(eventoRepository.findEventosByEspacio(dto.getIdEspacio()));
//            Set<Long> idsVistos = new HashSet<>();
//            for(DTOEvento evento : resultadoFinal){
//                if (idsVistos.add(evento.getId())) { // add() devuelve true si no estaba antes
//                    eventos.add(evento); // solo agrego si no se había visto antes
//                }
//            }
//            resultadoFinal=new HashSet<>(eventos);
        } else {
            // Iniciar con el primer conjunto y hacer intersección con los demás
            resultadoFinal.addAll(setsPorFiltro.get(0));
            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i)); // Intersección de sets
            }
        }

        // --- Mapear a DTO ---
        List<DTOResultadoBusquedaEventosPorEspacio> eventosDto = new ArrayList<>();
        for (DTOEvento evento : resultadoFinal) {
            List<String> disciplinas = this.eventoRepository.findDisciplinasByEvento(evento.getId());

            eventosDto.add(DTOResultadoBusquedaEventosPorEspacio.builder()
                    .id(evento.getId())
                    .nombre(evento.getNombre())
                    .fechaHoraInicio(evento.getFechaHoraInicio() == null
                            ? null
                            : evento.getFechaHoraInicio().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .precio(evento.getPrecioInscripcion() != null
                            ? evento.getPrecioInscripcion().doubleValue()
                            : 0)
                    .disciplinas(disciplinas)
                    .build());
        }

        return eventosDto;
    }

    @Override
    public List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEspacio, String texto)throws Exception {
        String[] palabras = texto.split(" ");
        List<Usuario> usuarios = new ArrayList<>();
        for (String palabra : palabras) {
            if (palabra.length() > 2) {
                usuarios.addAll(this.usuarioRepository.buscarUsuariosNoAdministradoresEspacio(idEspacio, palabra));
            }
        }
        // Eliminar duplicados por ID
        List<Usuario> usuariosUnicos = usuarios.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(Usuario::getId, e -> e, (e1, e2) -> e1),
                        m -> new ArrayList<>(m.values())
                ));
        List<DTOBusquedaUsuario> dtoBusquedaUsuarios = new ArrayList<>();
        for (Usuario usuario : usuariosUnicos) {
            dtoBusquedaUsuarios.add(DTOBusquedaUsuario.builder()
                    .nombre(usuario.getNombre())
                    .apellido(usuario.getApellido())
                    .dni(usuario.getDni())
                    .username(usuario.getUsername())
                    .build());
        }
        return dtoBusquedaUsuarios;
    }

    @Override
    public DTOAdministradoresEspacio obtenerAdministradoresEspacio (Long idEspacio, String username)throws Exception {
        List<Usuario> usuarios = this.usuarioRepository.buscarUsuariosAdministradoresEspacio(idEspacio);

        DTOAdministradoresEspacio dtoAdministradoresEspacio=DTOAdministradoresEspacio.builder()
                .esPropietario(this.espacioRepository.existsByIdAndPropietarioAdmin_Username(idEspacio, username, "Propietario"))
                .build();
        List<DTOAdministradoresEspacio.DTOAdministradores> dtoAdministradores=new ArrayList<>();
        for (Usuario usuario : usuarios) {
            List<DTOAdministradoresEspacio.DTOAdministradores.HistoricoDTO> historicoDTO=new ArrayList<>();
            DTOAdministradoresEspacio.DTOAdministradores dtoAdministrador= DTOAdministradoresEspacio.DTOAdministradores.builder()
                    .nombreApellido(usuario.getNombre() + " " + usuario.getApellido())
                    .username(usuario.getUsername())
                    .esPropietario(this.espacioRepository.existsByIdAndPropietarioAdmin_Username(idEspacio, usuario.getUsername(), "Propietario"))
                    .build();
            if(usuario.getFotoPerfil()!=null){
                String base64Image = encodeFileToBase64(usuario.getFotoPerfil());
                String[] parts = base64Image.split(",");
                String base64Data = parts[1];
                String mimeType = parts[0].split(";")[0].split(":")[1];
                String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                dtoAdministrador.setContentType(contentType);
                dtoAdministrador.setUrlFotoPerfil(base64Data);
            }

            for (AdministradorEspacio administrador : usuario.getAdministradoresEspacio()){
                historicoDTO.add(DTOAdministradoresEspacio.DTOAdministradores.HistoricoDTO.builder()
                                .fechaDesde(administrador.getFechaHoraAlta() == null ?
                                        null : administrador.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .fechaHasta(administrador.getFechaHoraBaja() == null ?
                                        null : administrador.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build());
            }
            dtoAdministrador.setFechasAdministracion(historicoDTO);
            dtoAdministradores.add(dtoAdministrador);
            historicoDTO=new ArrayList<>();
        }
        dtoAdministradoresEspacio.setDtoAdministradores(dtoAdministradores);
        return dtoAdministradoresEspacio;
    }

    @Override
    public void eliminarAdministradorEspacio(Long idEspacio, String username)throws Exception{
        AdministradorEspacio administradorEspacio = this.administradorEspacioRepository.findByEspacioAndUser(idEspacio, username);
        administradorEspacio.setFechaHoraBaja(LocalDateTime.now());
        this.administradorEspacioRepository.save(administradorEspacio);
    }

    @Override
    public void agregarAdministradorEspacio(Long idEspacio, String username)throws Exception{
        Usuario usuario=this.usuarioRepository.findByUsername(username).get();
        Espacio espacio=this.espacioRepository.findById(idEspacio).get();
        TipoAdministradorEspacio tipoAdministradorEspacio=this.tipoAdministradorEspacioRepository.findByNombre("Administrador").get();
        AdministradorEspacio administradorEspacio=AdministradorEspacio.builder()
                .usuario(usuario)
                .espacio(espacio)
                .tipoAdministradorEspacio(tipoAdministradorEspacio)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        this.administradorEspacioRepository.save(administradorEspacio);
    }

    @Override
    public void entregarPropietario(Long idEspacio, String username, String usernamePropietario)throws Exception{
        TipoAdministradorEspacio tipoAdministradorEspacio=this.tipoAdministradorEspacioRepository.findByNombre("Propietario").get();
        AdministradorEspacio administradorEspacioPropietarioActual=this.administradorEspacioRepository.findByEspacioAndUser(idEspacio, usernamePropietario);
        AdministradorEspacio administradorEspacioAdminActual=this.administradorEspacioRepository.findByEspacioAndUser(idEspacio, username);
        Usuario usuario=this.usuarioRepository.findByUsername(username).get();
        Usuario usuarioPropietario=this.usuarioRepository.findByUsername(usernamePropietario).get();
        Espacio espacio=this.espacioRepository.findById(idEspacio).get();

        if(administradorEspacioPropietarioActual.getTipoAdministradorEspacio()==tipoAdministradorEspacio){
            administradorEspacioPropietarioActual.setFechaHoraBaja(LocalDateTime.now());
            this.administradorEspacioRepository.save(administradorEspacioPropietarioActual);

            administradorEspacioAdminActual.setFechaHoraBaja(LocalDateTime.now());
            this.administradorEspacioRepository.save(administradorEspacioAdminActual);

            TipoAdministradorEspacio tipoAdministradorEspacioAdmin=this.tipoAdministradorEspacioRepository.findByNombre("Administrador").get();
            AdministradorEspacio administradorEspacioNuevo=AdministradorEspacio.builder()
                    .usuario(usuario)
                    .tipoAdministradorEspacio(tipoAdministradorEspacio)
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            this.administradorEspacioRepository.save(administradorEspacioNuevo);

            AdministradorEspacio administradorEspacioNuevo2=AdministradorEspacio.builder()
                    .usuario(usuarioPropietario)
                    .tipoAdministradorEspacio(tipoAdministradorEspacioAdmin)
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            this.administradorEspacioRepository.save(administradorEspacioNuevo2);
        }
    }

    @Override
    public void agregarEncargadoSubespacio(Long idSubEspacio, String username)throws Exception{
        SubEspacio subEspacio = this.subEspacioRepository.findById(idSubEspacio).get();
        Usuario usuario = this.usuarioRepository.findByUsername(username).get();
        EncargadoSubEspacio encargadoSubEspacio = EncargadoSubEspacio.builder()
                .subEspacio(subEspacio)
                .usuario(usuario)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        this.encargadoSubEspacioRepository.save(encargadoSubEspacio);
    }

    @Override
    public List<DTOEncargadoSubespacio>obtenerEncargadosSubespacios(Long idEspacio)throws Exception{
        List<SubEspacio> subEspacios=this.espacioRepository.findEncargadoByEspacio(idEspacio);
        List<DTOEncargadoSubespacio>encargados=new ArrayList<>();
        for(SubEspacio subEspacio:subEspacios){
            DTOEncargadoSubespacio encargadoSubespacio=DTOEncargadoSubespacio.builder()
                    .idSubespacio(subEspacio.getId())
                    .nombreSubespacio(subEspacio.getNombre())
                    .build();

            if(subEspacio.getEncargadoSubEspacio()!=null){
                if(subEspacio.getEncargadoSubEspacio().getFechaHoraBaja()==null){
                    encargadoSubespacio.setNombreApellidoEncargado(subEspacio.getEncargadoSubEspacio().getUsuario().getNombre() + subEspacio.getEncargadoSubEspacio().getUsuario().getApellido());
                    encargadoSubespacio.setUsername(subEspacio.getEncargadoSubEspacio().getUsuario().getUsername());
                    if(subEspacio.getEncargadoSubEspacio().getUsuario().getFotoPerfil()!=null){
                        String base64Image = encodeFileToBase64(subEspacio.getEncargadoSubEspacio().getUsuario().getFotoPerfil());
                        String[] parts = base64Image.split(",");
                        String base64Data = parts[1];
                        String mimeType = parts[0].split(";")[0].split(":")[1];
                        String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                        encargadoSubespacio.setContentType(contentType);
                        encargadoSubespacio.setUrlFotoPerfil(base64Data);
                    }
                }
            }
            encargados.add(encargadoSubespacio);
        }
        return encargados;
    }



    //Región de métodos auxiliares
    private void validarDatosCreacion (DTOCrearEspacio dtoEspacio) throws Exception {
        if (dtoEspacio == null) throw new Exception("Payload requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 50)
            throw new Exception("La dirección no debe superar 50 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
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
        if (otrosEspacios > 0) throw new Exception("Ya existe otro espacio con estos datos");

        Set<String> nombres = new HashSet<>();

        for (var subEspacio : dtoEspacio.getSubEspacios()) {
            String nombreNormalizado = subEspacio.getNombre().trim().toLowerCase();
            if (!nombres.add(nombreNormalizado)) {
                throw new Exception("No puede haber dos subespacios con el mismo nombre: " + subEspacio.getNombre());
            }
        }
    }

    private void validarDatosEdicion (DTOEspacioEditar dtoEspacio) throws Exception {
        if (dtoEspacio == null) throw new Exception("Payload requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 50)
            throw new Exception("La dirección no debe superar 50 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
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
        if (otrosEspacios > 0) throw new Exception("Ya existe otro espacio con estos datos");

        Set<String> nombres = new HashSet<>();

        for (var subEspacio : dtoEspacio.getSubEspacios()) {
            String nombreNormalizado = subEspacio.getNombre().trim().toLowerCase();
            if (!nombres.add(nombreNormalizado)) {
                throw new Exception("No puede haber dos subespacios con el mismo nombre: " + subEspacio.getNombre());
            }
        }
    }

    private String guardarArchivo (MultipartFile archivo, String nombreEspacio) throws IOException {
//        File directorio = new File(directorioBase);
//        if (!directorio.exists()) {
//            directorio.mkdirs();
//        }

                if (!Files.exists(Paths.get(directorioBase))) {
                    Files.createDirectories(Paths.get(directorioBase));
                }

                String nombreArchivo = nombreEspacio + "_" + archivo.getOriginalFilename();
                //Path rutaDestino = Path.of(directorioBase, nombreArchivo);
                Path filePath = Paths.get(directorioBase).resolve(nombreArchivo).toAbsolutePath().normalize();
                Files.write(filePath, archivo.getBytes());

                //archivo.transferTo(rutaDestino.toFile());

                return nombreArchivo;
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

    private void eliminarArchivoFisico(String ruta) {
        try {
            if (ruta != null) Files.deleteIfExists(Paths.get(ruta));
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo físico: " + ruta);
        }
    }
}