package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.estadoSEP.DTOEstadoSEP;
import com.evtnet.evtnetback.dto.imagenes.DTOActualizarImagenesEspacio;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import com.evtnet.evtnetback.dto.espacios.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.util.MercadoPagoSingleton;
import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
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
    private final ResenaEspacioRepository resenaEspacioRepository;
    private final IconoCaracteristicaRepository iconoCaracteristicaRepository;
    private final RegistroSingleton registroSingleton;
    private final ChatRepository chatRepo;
    private final MercadoPagoSingleton mercadoPagoSingleton;


    @Value("${app.storage.documentacion:/app/storage/documentacion}")
    private String directorioBase;

    @Value("${app.storage.perfiles:/app/storage/perfiles}")
    private String directorioPerfiles;


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
            ResenaEspacioRepository resenaEspacioRepository,
            IconoCaracteristicaRepository iconoCaracteristicaRepository,
            RegistroSingleton registroSingleton,
            ChatRepository chatRepo,
            MercadoPagoSingleton mercadoPagoSingleton
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
        this.resenaEspacioRepository = resenaEspacioRepository;
        this.iconoCaracteristicaRepository = iconoCaracteristicaRepository;
        this.registroSingleton = registroSingleton;
        this.chatRepo = chatRepo;
        this.mercadoPagoSingleton = mercadoPagoSingleton;
    }

    @Override
    public Long crearEspacio(DTOCrearEspacio dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception{
        validarDatosCreacion(dtoEspacio, documentacion);
        String rutaBasesYCondiciones="";
        if(basesYCondiciones!=null && !basesYCondiciones.isEmpty())
            rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());

        TipoEspacio tipoEspacio = this.tipoEspacioRepository.findByNombre("Privado").get();

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

        espacio = save(espacio);

        SolicitudEspacioPublico solicitud;
        if(dtoEspacio.getSepId()!=null && dtoEspacio.getSepId()>0 ){
            solicitud=this.solicitudEspacioPublicoRepository.findById(dtoEspacio.getSepId()).get();
            solicitud.setEspacio(espacio);
            this.solicitudEspacioPublicoRepository.save(solicitud);
        }

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
        // Creacion de chat
        Chat chat = Chat.builder()
        .tipo(Chat.Tipo.ESPACIO)
        .fechaHoraAlta(LocalDateTime.now())
        .espacio(espacio)
        .build();

        chatRepo.save(chat);


        registroSingleton.write("Espacios", "espacio_privado", "creacion", "Espacio de ID " + espacio.getId() + " nombre" +espacio.getNombre()+ "'");
        return espacio.getId();
    }

    @Override
    public Long crearEspacioPublico(DTOCrearEspacio dtoEspacio) throws Exception{
        validarDatosCreacionEspacioPublido(dtoEspacio);

        TipoEspacio tipoEspacio = this.tipoEspacioRepository.findByNombre("Público").get();

        Espacio espacio = Espacio.builder()
                .nombre(dtoEspacio.getNombre())
                .descripcion(dtoEspacio.getDescripcion())
                .direccionUbicacion(dtoEspacio.getDireccion())
                .latitudUbicacion(new BigDecimal(dtoEspacio.getLatitud()))
                .longitudUbicacion(new BigDecimal(dtoEspacio.getLongitud()))
                .fechaHoraAlta(LocalDateTime.now())
                .tipoEspacio(tipoEspacio)
                .requiereAprobarEventos(false)
                .build();

        espacio = save(espacio);

        SolicitudEspacioPublico solicitud;
        if(dtoEspacio.getSepId()!=null && dtoEspacio.getSepId()>0 ){
            solicitud=this.solicitudEspacioPublicoRepository.findById(dtoEspacio.getSepId()).get();
            solicitud.setEspacio(espacio);
            this.solicitudEspacioPublicoRepository.save(solicitud);
        }

        AdministradorEspacio administrador = AdministradorEspacio.builder()
                .espacio(espacio)
                .usuario(this.usuarioRepository.findByUsername(dtoEspacio.getUsername()).get())
                .tipoAdministradorEspacio(this.tipoAdministradorEspacioRepository.findByNombre("Administrador").get())
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        this.administradorEspacioRepository.save(administrador);

        EspacioEstado espacioEstado = EspacioEstado.builder()
                .estadoEspacio(this.estadoEspacioRepository.findByNombre("Habilitado").get())
                .espacio(espacio)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        this.espacioEstadoRepository.save(espacioEstado);

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
        registroSingleton.write("Espacios", "espacio_publico", "creacion", "Espacio de ID " + espacio.getId() + " nombre" +espacio.getNombre()+ "'");
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
                .idChat(espacio.getChat() != null ? espacio.getChat().getId() : null)
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
        boolean esProp=this.espacioRepository.existsByIdAndPropietarioAdmin_Username(id, username, "Propietario");

        if (!esAdmin && !esProp) {
            throw new Exception("No tiene permiso para administrar este espacio");
        }

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

        DTOEspacioEditar espacioDTO= DTOEspacioEditar.builder()
                .id(id)
                .nombre(espacio.getNombre())
                .descripcion(espacio.getDescripcion())
                .direccion(espacio.getDireccionUbicacion())
                .latitud(espacio.getLatitudUbicacion().doubleValue())
                .longitud(espacio.getLongitudUbicacion().doubleValue())
                .subEspacios(dtoSubespacios)
                .esAdmin(esAdmin)
                .esPropietario(esProp)
                .esPublico(tipoEspacio.getNombre().equalsIgnoreCase("público"))
                .estado(DTOEspacioEstado.builder()
                        .id(espacioEstado.getEstadoEspacio().getId())
                        .nombre(espacioEstado.getEstadoEspacio().getNombre())
                        .descripcion(espacioEstado.getDescripcion())
                        .build())
                .documentacion(archivos)
                .estadosEspacio(estadosTransicion)
                .requiereAprobacion(espacio.getRequiereAprobarEventos())
                .build();

        DTOArchivo archivo;
        if(espacio.getBasesYCondiciones() != null && !espacio.getBasesYCondiciones().isEmpty()){
            byte[] content = Files.readAllBytes(Paths.get(directorioBase, espacio.getBasesYCondiciones()));
            archivo = DTOArchivo.builder()
                    .nombreArchivo(espacio.getBasesYCondiciones())
                    .base64(Base64.getEncoder().encodeToString(content))
                    .build();
            espacioDTO.setBasesYCondiciones(archivo);
        }
        return espacioDTO;
    }

    @Override
    public void editarEspacio(DTOEspacioEditar dtoEspacio, MultipartFile basesYCondiciones, List<MultipartFile> documentacion) throws Exception{
        Espacio espacio = this.espacioRepository.findById(dtoEspacio.getId())
                .orElseThrow(() -> new Exception("Espacio no encontrado"));

        validarDatosEdicion(dtoEspacio, documentacion);

        if (basesYCondiciones != null && !basesYCondiciones.isEmpty()) {
            String rutaBasesYCondiciones = guardarArchivo(basesYCondiciones, dtoEspacio.getNombre());
            espacio.setBasesYCondiciones(rutaBasesYCondiciones);
        }

        espacio.setNombre(dtoEspacio.getNombre());
        espacio.setDescripcion(dtoEspacio.getDescripcion());
        espacio.setDireccionUbicacion(dtoEspacio.getDireccion());
        espacio.setLatitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLatitud()));
        espacio.setLongitudUbicacion(BigDecimal.valueOf(dtoEspacio.getLongitud()));

        espacio = update(dtoEspacio.getId(),espacio);

        EspacioEstado espacioEstadoActual =  this.espacioEstadoRepository.findActualByEspacio(dtoEspacio.getId());

        if(espacioEstadoActual.getEstadoEspacio().getId()!=dtoEspacio.getEstado().getId() || espacioEstadoActual.getEstadoEspacio().getNombre().equals("Observado")){
            espacioEstadoActual.setFechaHoraBaja(LocalDateTime.now());
            this.espacioEstadoRepository.save(espacioEstadoActual);
            EstadoEspacio estadoEspacio;
            if(espacioEstadoActual.getEstadoEspacio().getNombre().equals("Observado"))
                estadoEspacio=this.estadoEspacioRepository.findByNombre("En_revisión").orElseThrow(() -> new Exception("Estado de Espacio no encontrado"));
            else estadoEspacio=this.estadoEspacioRepository.findByNombre(dtoEspacio.getEstado().getNombre()).get();
            EspacioEstado espacioEstado = EspacioEstado.builder()
                    .estadoEspacio(estadoEspacio)
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            this.espacioEstadoRepository.save(espacioEstado);
        }

        List<DocumentacionEspacio> documentosExistentes = this.documentacionEspacioRepository.findByEspacioId(espacio.getId());

        List<Long> idsDocumentosMantener = dtoEspacio.getDocumentacion() != null
                ? dtoEspacio.getDocumentacion().stream()
                .map(DTOArchivo::getId)
                .filter(Objects::nonNull)
                .toList()
                : new ArrayList<>();

        for (DocumentacionEspacio documento : documentosExistentes) {
            if (!idsDocumentosMantener.contains(documento.getId())) {
                documento.setFechaHoraBaja(LocalDateTime.now());
                this.documentacionEspacioRepository.save(documento);
                //eliminarArchivoFisico(documento.getDocumentacion());
            }
        }

        if (documentacion != null && !documentacion.isEmpty()) {
            for (MultipartFile file : documentacion) {
                if (file != null && !file.isEmpty()) {
                    String ruta = guardarArchivo(file, dtoEspacio.getNombre());
                    DocumentacionEspacio nuevaDocumentacion = DocumentacionEspacio.builder()
                            .espacio(espacio)
                            .documentacion(ruta)
                            .fechaHoraAlta(LocalDateTime.now())
                            .build();
                    this.documentacionEspacioRepository.save(nuevaDocumentacion);
                }
            }
        }

        List<SubEspacio> subEspaciosExistentes = this.subEspacioRepository.findAllByEspacio(espacio.getId());


        List<Long> idsSubespaciosEnviados = dtoEspacio.getSubEspacios().stream()
                .map(DTOSubespacioEditar::getId)
                .filter(Objects::nonNull)
                .toList();


        for (SubEspacio subEspacio : subEspaciosExistentes) {
            if (!idsSubespaciosEnviados.contains(subEspacio.getId())) {
                subEspacio.setFechaHoraBaja(LocalDateTime.now());
                this.subEspacioRepository.save(subEspacio);
            }
        }

        for (DTOSubespacioEditar dtoSubEspacio : dtoEspacio.getSubEspacios()) {
            SubEspacio subEspacio;
            if (dtoSubEspacio.getId() != null) {
                subEspacio = this.subEspacioRepository.findById(dtoSubEspacio.getId())
                        .orElseThrow(() -> new Exception("SubEspacio no encontrado: " + dtoSubEspacio.getNombre()));
            } else {
                subEspacio = SubEspacio.builder()
                        .espacio(espacio)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
            }

            subEspacio.setNombre(dtoSubEspacio.getNombre());
            subEspacio.setDescripcion(dtoSubEspacio.getDescripcion());
            subEspacio.setCapacidadmaxima(dtoSubEspacio.getCapacidadMaxima());


            List<DisciplinaSubEspacio> disciplinasExistentes = subEspacio.getDisciplinasSubespacio();

            if (disciplinasExistentes == null) {
                disciplinasExistentes = new ArrayList<>();
            }


            List<Long> idsDisciplinasEnviadas = dtoSubEspacio.getDisciplinas() != null
                    ? dtoSubEspacio.getDisciplinas().stream().map(DTODisciplinas::getId).toList()
                    : new ArrayList<>();


            disciplinasExistentes.removeIf(dse -> !idsDisciplinasEnviadas.contains(dse.getDisciplina().getId()));


            for (DTODisciplinas dtoDisciplina : dtoSubEspacio.getDisciplinas()) {
                boolean existe = disciplinasExistentes.stream()
                        .anyMatch(dse -> dse.getDisciplina().getId().equals(dtoDisciplina.getId()));
                if (!existe) {
                    disciplinasExistentes.add(DisciplinaSubEspacio.builder()
                            .disciplina(this.disciplinaRepository.findById(dtoDisciplina.getId())
                                    .orElseThrow(() -> new Exception("Disciplina no encontrada: " + dtoDisciplina.getId())))
                            .build());
                }
            }

            subEspacio.setDisciplinasSubespacio(disciplinasExistentes);
            this.subEspacioRepository.save(subEspacio);
            registroSingleton.write("Espacios", "espacio_privado", "modificacion", "Espacio de ID " + espacio.getId() + " nombre" +espacio.getNombre()+ "'");

        }


    }

    @Override
    public void dejarDeAdministrar(Long id, String username)throws Exception{
        AdministradorEspacio administradorEspacio = this.administradorEspacioRepository.findByEspacioAndUser(id, username);
        administradorEspacio.setFechaHoraBaja(LocalDateTime.now());
        this.administradorEspacioRepository.save(administradorEspacio);
        registroSingleton.write("Espacios", "administrador_espacio_privado", "eliminacion", "AdministradorEspacio de ID " + id + " username" +username+ "'");

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

        if (dtoEspacio.getTipos() != null && !dtoEspacio.getTipos().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(espacioRepository.findEspaciosByTipo(dtoEspacio.getTipos())));
        }

        if (dtoEspacio.getDisciplinas() != null && !dtoEspacio.getDisciplinas().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(espacioRepository.findEspaciosByDisciplina(dtoEspacio.getDisciplinas())));
        }

        if (setsPorFiltro.isEmpty()) {
            resultadoFinal.addAll(espacioRepository.findAllHabilitados());
        } else {
            resultadoFinal.addAll(setsPorFiltro.get(0));

            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i));
            }
        }

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

        if (dto.getPrecioLimite() >0) {
            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByPrecio(dto.getIdEspacio(), new BigDecimal(dto.getPrecioLimite()))));
        }

        if (dto.getDisciplinas() != null && !dto.getDisciplinas().isEmpty()) {
            setsPorFiltro.add(new HashSet<>(eventoRepository.findEventosByDisciplinas(dto.getIdEspacio(), dto.getDisciplinas())));
        }


        if (setsPorFiltro.isEmpty()) {
            resultadoFinal.addAll(eventoRepository.findEventosByEspacio(dto.getIdEspacio()));
        } else {
            resultadoFinal.addAll(setsPorFiltro.get(0));
            for (int i = 1; i < setsPorFiltro.size(); i++) {
                resultadoFinal.retainAll(setsPorFiltro.get(i));
            }
        }

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
                    .estado(evento.getEstado())
                    .requiereAprobacion(this.espacioRepository.findById(dto.getIdEspacio()).get().getRequiereAprobarEventos())
                    .subespacio(eventoRepository.findById(evento.getId()).get().getSubEspacio().getNombre())
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
                Path path = Paths.get(directorioPerfiles);
                String base64Image = encodeFileToBase64(path.resolve(usuario.getFotoPerfil()).toAbsolutePath().toString());
                String[] parts = base64Image.split(",");
                String base64Data = parts[1];
                String mimeType = parts[0].split(";")[0].split(":")[1];
                String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                dtoAdministrador.setContentType(contentType);
                dtoAdministrador.setUrlFotoPerfil(base64Data);
            } else {
                File file = new File(getClass().getResource("/default.png").getFile());
                Path path = file.toPath();

                String base64Image = encodeFileToBase64(path.toAbsolutePath().toString());
                String[] parts = base64Image.split(",");
                String base64Data = parts[1];
                String mimeType = parts[0].split(";")[0].split(":")[1];
                String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
                dtoAdministrador.setContentType(contentType);
                dtoAdministrador.setUrlFotoPerfil(base64Data);
            }

            for (AdministradorEspacio administrador : usuario.getAdministradoresEspacio()){
                if (administrador.getEspacio().getId().longValue() != idEspacio.longValue()) continue;
                historicoDTO.add(DTOAdministradoresEspacio.DTOAdministradores.HistoricoDTO.builder()
                                .fechaDesde(administrador.getFechaHoraAlta() == null ?
                                        null : administrador.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                .fechaHasta(administrador.getFechaHoraBaja() == null ?
                                        null : administrador.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build());
            }
            historicoDTO.sort((f1, f2) -> {
                long t1 = f1.getFechaDesde() != null ? f1.getFechaDesde() : 0L;
                long t2 = f2.getFechaDesde() != null ? f2.getFechaDesde() : 0L;
                return Long.compare(t2, t1); // descendente
            });

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
        registroSingleton.write("Espacios", "administrador_espacio_privado", "eliminacion", "AdministradorEspacio de ID " + administradorEspacio.getId() + " username" +username+ "'");
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
        administradorEspacio=this.administradorEspacioRepository.save(administradorEspacio);
        registroSingleton.write("Espacios", "administrador_espacio_privado", "creacion", "AdministradorEspacio de ID " + administradorEspacio.getId() + " username" +username+ "'");
    }

    @Override
    public void entregarPropietario(Long idEspacio, String username, String usernamePropietario)throws Exception{
        TipoAdministradorEspacio tipoAdministradorEspacio=this.tipoAdministradorEspacioRepository.findByNombre("Propietario").get();
        AdministradorEspacio administradorEspacioPropietarioActual=this.administradorEspacioRepository.findByEspacioAndUser(idEspacio, usernamePropietario);
        AdministradorEspacio administradorEspacioAdminActual=this.administradorEspacioRepository.findByEspacioAndUser(idEspacio, username);
        Usuario usuario=this.usuarioRepository.findByUsername(username).get();
        Usuario usuarioPropietario=this.usuarioRepository.findByUsername(usernamePropietario).get();
        Espacio espacio=this.espacioRepository.findById(idEspacio).get();

        if (!mercadoPagoSingleton.checkUsuarioAutorizado(usuario)) {
            throw new Exception("No se pudo asignar a este usuario como propietario dado que no ha vinculado su cuenta de Mercado Pago. Solicítele que haga esto para poder continuar.");
        }

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
            administradorEspacioNuevo=this.administradorEspacioRepository.save(administradorEspacioNuevo);

            AdministradorEspacio administradorEspacioNuevo2=AdministradorEspacio.builder()
                    .usuario(usuarioPropietario)
                    .tipoAdministradorEspacio(tipoAdministradorEspacioAdmin)
                    .espacio(espacio)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            administradorEspacioNuevo2=this.administradorEspacioRepository.save(administradorEspacioNuevo2);
            registroSingleton.write("Espacios", "administrador_espacio_privado", "eliminacion", "AdministradorEspacio de ID (propietario)" + administradorEspacioNuevo.getId() + " username" +usernamePropietario+ "'");
            registroSingleton.write("Espacios", "administrador_espacio_privado", "creacion", "AdministradorEspacio de ID (nuevo propietario)" + administradorEspacioNuevo2.getId() + " username" +username+ "'");
        }
    }

    @Override
    public void agregarEncargadoSubespacio(Long idSubEspacio, String username)throws Exception{
        SubEspacio subEspacio = this.subEspacioRepository.findById(idSubEspacio).get();
        Usuario usuario = this.usuarioRepository.findByUsername(username).get();
        subEspacio.setEncargadoSubEspacio(usuario);
        this.subEspacioRepository.save(subEspacio);
        registroSingleton.write("Espacios", "encargado_subespacio", "creacion", "Usuario de ID " + usuario.getId() + " username" +username+ "'");
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
                    encargadoSubespacio.setNombreApellidoEncargado(subEspacio.getEncargadoSubEspacio().getNombre() + subEspacio.getEncargadoSubEspacio().getApellido());
                    encargadoSubespacio.setUsername(subEspacio.getEncargadoSubEspacio().getUsername());
                    if(subEspacio.getEncargadoSubEspacio().getFotoPerfil()!=null){
                        Path path = Paths.get(directorioPerfiles);
                        String base64Image = encodeFileToBase64(path.resolve(subEspacio.getEncargadoSubEspacio().getFotoPerfil()).toAbsolutePath().toString());
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

    @Override
    public DTOResenasEspacio obtenerResenasEspacio(Long idEspacio)throws Exception{
        List<ResenaEspacio> resenas=this.resenaEspacioRepository.resenasByEspacio(idEspacio);
        List<DTOResenasEspacio.DTOResenaEspacio> dtoResenasEspacios=new ArrayList<>();
        for(ResenaEspacio resena:resenas){
            dtoResenasEspacios.add(DTOResenasEspacio.DTOResenaEspacio.builder()
                    .titulo(resena.getTitulo())
                    .fecha(resena.getFechaHora() == null
                            ? null
                            : resena.getFechaHora().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .puntuacion(resena.getPuntaje())
                    .comentario(resena.getComentario())
                    .username(resena.getUsuario().getUsername())
                    .usuario(resena.getUsuario().getNombre() + " " + resena.getUsuario().getApellido())
                    .build());
        }

        List<DTOResenasEspacio.Puntuacion> puntuaciones=new ArrayList<>();
        for(int i=1; i<=5; i++){
            final int puntaje=i;
            puntuaciones.add(DTOResenasEspacio.Puntuacion.builder()
                            .puntuacion(i)
                            .cantidad(resenas.stream().filter(r->Objects.equals(r.getPuntaje(),puntaje)).count())
                    .build());
        }
        return DTOResenasEspacio.builder()
                .resenas(dtoResenasEspacios)
                .puntuaciones(puntuaciones)
                .build();
    }

    @Override
    public List<DTOEstadoEspacio> obtenerEstadosEspacio() throws Exception {
        List<EstadoEspacio> estadosEspacio = estadoEspacioRepository.findAll();
        return estadosEspacio.stream()
                .map(me->DTOEstadoEspacio.builder()
                        .id(me.getId())
                        .nombre(me.getNombre())
                        .build()
                ).toList();
    }

    @Override
    public void crearResenaEspacio(DTOCrearResenaEspacio dto, String username)throws Exception{
        if(dto.getIdEspacio()==null & dto.getComentario().isEmpty() && dto.getTitulo().isEmpty() && dto.getPuntuacion()==null)
            throw new Exception("Los datos no pueden estar vacíos");

        Usuario usuario=this.usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));
        Espacio espacio=this.espacioRepository.findById(dto.getIdEspacio()).orElseThrow(() -> new Exception("Espacio no encontrado"));
        ResenaEspacio resena = ResenaEspacio.builder()
                .titulo(dto.getTitulo())
                .puntaje(dto.getPuntuacion())
                .comentario(dto.getComentario())
                .fechaHora(LocalDateTime.now())
                .espacio(espacio)
                .usuario(usuario)
                .build();
        resena=this.resenaEspacioRepository.save(resena);
        registroSingleton.write("Espacios", "reseña", "creacion", "Espacio de ID " + dto.getIdEspacio() + " username" +username+ " Reseña de ID "+resena.getId()+ "'");
    }

    @Override
    public List<DTOBusquedaEspacio>buscarEspaciosPropios(String username)throws Exception{
        List<Espacio>espaciosPropios=this.espacioRepository.findEspaciosPropios(username);
        if(espaciosPropios.isEmpty()) throw new Exception("No tiene espacios propios");
        List<DTOBusquedaEspacio>dtoEspaciosPropios=new ArrayList<>();
        for(Espacio espacio:espaciosPropios){
            DTOBusquedaEspacio dtoBusquedaEspacio=DTOBusquedaEspacio.builder()
                    .id(espacio.getId())
                    .direccion(espacio.getDireccionUbicacion())
                    .nombre(espacio.getNombre())
                    .build();

            Set<String> disciplinasSet = new HashSet<>();

            for (SubEspacio subEspacio : espacio.getSubEspacios()) {
                disciplinasSet.addAll(this.disciplinaSubEspacioRepository.disciplinasNombre(subEspacio.getId()));
            }
            dtoBusquedaEspacio.setDisciplinas(new ArrayList<>(disciplinasSet));
            dtoEspaciosPropios.add(dtoBusquedaEspacio);
        }
        return dtoEspaciosPropios;
    }

    @Override
    public void actualizarCarateristicasEspacio(DTOActualizarCaracteristicasSubespacio dtoCaracteristicaSubEspacio) throws Exception {
        if (dtoCaracteristicaSubEspacio.getCaracteristicas().isEmpty()) {
            throw new Exception("Lista de características vacía");
        }

        SubEspacio subEspacio = this.subEspacioRepository.findById(dtoCaracteristicaSubEspacio.getIdSubEspacio())
                .orElseThrow(() -> new Exception("Subespacio no encontrado"));

        List<Caracteristica> caracteristicasActuales = this.caracteristicaRepository.findBySubEspacio(subEspacio.getId());

        Set<Long> idsDelDTO = dtoCaracteristicaSubEspacio.getCaracteristicas().stream()
                .map(DTOActualizarCaracteristicasSubespacio.DTOCaracteristicas::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Caracteristica existente : caracteristicasActuales) {
            if (!idsDelDTO.contains(existente.getId())) {
                this.caracteristicaRepository.delete(existente);
            }
        }

        for (DTOActualizarCaracteristicasSubespacio.DTOCaracteristicas dto : dtoCaracteristicaSubEspacio.getCaracteristicas()) {

            IconoCaracteristica icono = this.iconoCaracteristicaRepository.findById(dto.getIdIconoCaracteristica())
                    .orElseThrow(() -> new Exception("Ícono de característica no encontrado"));

            Caracteristica caracteristica;

            if (dto.getId() != 0) {
                caracteristica = this.caracteristicaRepository.findById(dto.getId())
                        .orElseThrow(() -> new Exception("Característica no encontrada: " + dto.getId()));

                caracteristica.setIconoCaracteristica(icono);
                caracteristica.setNombre(dto.getNombre());
                caracteristica.setSubEspacio(subEspacio);
            } else {
                caracteristica = Caracteristica.builder()
                        .subEspacio(subEspacio)
                        .iconoCaracteristica(icono)
                        .nombre(dto.getNombre())
                        .build();
            }

            caracteristica=this.caracteristicaRepository.save(caracteristica);
            registroSingleton.write("Espacios", "caracteristica", "modificacion", "Espacio de ID " + subEspacio.getId() + " SubEspacio de ID" +subEspacio.getId()+ "'");
        }
    }

    @Override
    public byte[] obtenerBasesYCondiciones(Long idEspacio) throws Exception {
        var espacio = espacioRepository.findById(idEspacio)
                .orElseThrow(() -> new Exception("Espacio no encontrado"));

        if (espacio.getBasesYCondiciones() == null || espacio.getBasesYCondiciones().isEmpty()) {
            throw new Exception("El espacio no tiene bases y condiciones");
        }

        if (!espacio.getBasesYCondiciones().toLowerCase().endsWith(".pdf")) {
            throw new Exception("El archivo no es un PDF");
        }

        Path path = Paths.get(directorioBase, espacio.getBasesYCondiciones());

        if (!Files.exists(path)) {
            throw new Exception("Archivo de bases y condiciones no encontrado");
        }

        return Files.readAllBytes(path);
    }

    //Región de métodos auxiliares
    private void validarDatosCreacion (DTOCrearEspacio dtoEspacio, List<MultipartFile> documentacion) throws Exception {
        if (dtoEspacio == null) throw new Exception("Datos de espacio requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 150)
            throw new Exception("La dirección no debe superar 150 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoEspacio.getSubEspacios().isEmpty()) throw new Exception("Debe agregar al menos un subespacio");

        ParametroSistema parametroRangoUbicacion = this.parametroSistemaRepository.findByIdentificador("rango_validar_ubicacion").orElseThrow(() -> new Exception("Ocurrió un error"));

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
        if(documentacion.isEmpty()) throw new Exception("La documentación es requerida");

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión para crear un espacio"));
        if (!dtoEspacio.getUsername().equals(username)) {
            throw new Exception("Usuario incorrecto");
        }
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("No se encontró al usuario"));
        if (!mercadoPagoSingleton.checkUsuarioAutorizado(usuario)) {
            throw new Exception("Primero debe vincular su cuenta a Mercado Pago");
        }
    }

    private void validarDatosCreacionEspacioPublido (DTOCrearEspacio dtoEspacio) throws Exception {
        if (dtoEspacio == null) throw new Exception("Datos de espacio requerido");
        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 150)
            throw new Exception("La dirección no debe superar 150 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoEspacio.getSubEspacios().isEmpty()) throw new Exception("Debe agregar al menos un subespacio");

        ParametroSistema parametroRangoUbicacion = this.parametroSistemaRepository.findByIdentificador("rango_validar_ubicacion").orElseThrow(() -> new Exception("Ocurrió un error"));

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

    private void validarDatosEdicion (DTOEspacioEditar dtoEspacio, List<MultipartFile> documentacion) throws Exception {
        if (dtoEspacio == null) throw new Exception("Payload requerido");

        if (dtoEspacio.getNombre() == null || dtoEspacio.getNombre().isBlank())
            throw new Exception("El nombre es obligatorio");
        if (dtoEspacio.getNombre().length() > 50)
            throw new Exception("El nombre no debe superar 50 caracteres");
        if (dtoEspacio.getDireccion() == null || dtoEspacio.getDireccion().isBlank())
            throw new Exception("La dirección es obligatoria");
        if (dtoEspacio.getDireccion().length() > 150)
            throw new Exception("La dirección no debe superar 150 caracteres");
        if (dtoEspacio.getDescripcion() != null && dtoEspacio.getDescripcion().length() > 500)
            throw new Exception("La descripción no debe superar 500 caracteres");
        if (dtoEspacio.getLatitud() == 0 || dtoEspacio.getLongitud() == 0)
            throw new Exception("Debe indicar la ubicación (lat/lon)");
        if (dtoEspacio.getSubEspacios().isEmpty()) throw new Exception("Debe agregar al menos un subespacio");

        ParametroSistema parametroRangoUbicacion = this.parametroSistemaRepository.findByIdentificador("rango_validar_ubicacion").orElseThrow(() -> new Exception("Ocurrió un error"));

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

        if((documentacion==null || documentacion.isEmpty()) && (dtoEspacio.getDocumentacion()==null || dtoEspacio.getDocumentacion().isEmpty())) throw new Exception("La documentación es requerida");
    }

    private String guardarArchivo (MultipartFile archivo, String nombreEspacio) throws IOException {

        if(archivo!=null){
            if (!Files.exists(Paths.get(directorioBase))) {
                Files.createDirectories(Paths.get(directorioBase));
            }

            String nombreArchivo = nombreEspacio + "_" + archivo.getOriginalFilename();
            Path filePath = Paths.get(directorioBase).resolve(nombreArchivo).toAbsolutePath().normalize();
            Files.write(filePath, archivo.getBytes());
            return nombreArchivo;
        }
        return "";
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