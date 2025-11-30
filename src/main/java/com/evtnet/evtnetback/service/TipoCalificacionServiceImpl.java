package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import com.evtnet.evtnetback.dto.motivoCalificacion.DTOMotivoCalificacion;
import com.evtnet.evtnetback.entity.IconoCaracteristica;
import com.evtnet.evtnetback.entity.MotivoCalificacion;
import com.evtnet.evtnetback.entity.TipoCalificacion;
import com.evtnet.evtnetback.repository.MotivoCalificacionRepository;
import com.evtnet.evtnetback.repository.TipoCalificacionRepository;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacion;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacionSelect;
import com.evtnet.evtnetback.util.RegistroSingleton;
import com.evtnet.evtnetback.util.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TipoCalificacionServiceImpl extends BaseServiceImpl <TipoCalificacion, Long> implements TipoCalificacionService {

    @Value("${app.storage.tipoCalificacion:/app/storage/tipoCalificacion}")
    private String imagenesDirectorio;

    private final TipoCalificacionRepository tipoCalificacionRepository;
    private final ParametroSistemaService parametroSistemaService;
    private final MotivoCalificacionRepository motivoCalificacionRepository;
    private final RegistroSingleton registroSingleton;

    public TipoCalificacionServiceImpl(TipoCalificacionRepository tipoCalificacionRepository,
                                       ParametroSistemaService parametroSistemaService,
                                       MotivoCalificacionRepository motivoCalificacionRepository,
                                       RegistroSingleton registroSingleton) {
        super(tipoCalificacionRepository);
        this.tipoCalificacionRepository = tipoCalificacionRepository;
        this.parametroSistemaService = parametroSistemaService;
        this.motivoCalificacionRepository = motivoCalificacionRepository;
        this.registroSingleton = registroSingleton;
    }
    @Override
    public Page<DTOTipoCalificacion> obtenerListaTipoCalificacion(int page, boolean vigentes, boolean dadasDeBaja) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHoraBaja"),
                        Sort.Order.asc("fechaHoraAlta")
                )
        );
        Specification<TipoCalificacion> spec = Specification.where(null);
        boolean vigentesFiltro = Boolean.TRUE.equals(vigentes);
        boolean dadasDeBajaFiltro = Boolean.TRUE.equals(dadasDeBaja);

        if (vigentesFiltro && !dadasDeBajaFiltro) {
            spec = spec.and((root, cq, cb) -> cb.isNull(root.get("fechaHoraBaja")));
        }

        if (!vigentesFiltro && dadasDeBajaFiltro) {
            spec = spec.and((root, cq, cb) -> cb.isNotNull(root.get("fechaHoraBaja")));
        }

        Page<TipoCalificacion> tiposCalificacion = tipoCalificacionRepository.findAll(spec, pageable);
        Path imagenesPath = Paths.get(imagenesDirectorio);
        List<TipoCalificacion> filtrados = tiposCalificacion
                .stream()
                .filter(ic -> ic.getImagen() != null && Files.exists(imagenesPath.resolve(ic.getImagen())))
                .toList();
        List<DTOTipoCalificacion> dtos = filtrados.stream().map(ic -> {
            String base64Image = encodeFileToBase64(ic.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];

            String extension = "";
            String fileName = ic.getImagen();

            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
                extension = fileName.substring(dotIndex + 1).toLowerCase();
            }

            String contentType = extension.equals("svg") ? "svg" : "png";
            return DTOTipoCalificacion.builder()
                    .id(ic.getId())
                    .nombre(ic.getNombre())
                    .url(base64Data)
                    .fechaAlta(ic.getFechaHoraAlta()==null ? null
                            : TimeUtil.toMillis(ic.getFechaHoraAlta()))
                    .fechaBaja(ic.getFechaHoraBaja()==null ? null
                            : TimeUtil.toMillis(ic.getFechaHoraBaja()))
                    .contentType(contentType)
                    .build();
        }).toList();
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    @Override
    public List<DTOTipoCalificacionSelect> obtenerTiposCalificacionSelect() throws Exception {
        List<TipoCalificacion> tiposCalificacion = tipoCalificacionRepository.findAll();
        return tiposCalificacion.stream().map(tc->DTOTipoCalificacionSelect.builder()
                        .id(tc.getId())
                        .nombre(tc.getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public DTOTipoCalificacion obtenerTipoCalificacionCompleto(Long id) throws Exception {
        TipoCalificacion tipoCalificacion = tipoCalificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de calificación no encontrado"));

        String base64Image = encodeFileToBase64(tipoCalificacion.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];

        String extension = "";
        String fileName = tipoCalificacion.getImagen();

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        String contentType = extension.equals("svg") ? "svg" : "png";
        DTOTipoCalificacion dto= DTOTipoCalificacion.builder()
                .id(tipoCalificacion.getId())
                .nombre(tipoCalificacion.getNombre())
                .url(base64Data)
                .contentType(contentType)
                .build();
        List<DTOMotivoCalificacion> dtoMotivoCalificacions=new ArrayList<>();
        for(MotivoCalificacion motivo: tipoCalificacion.getMotivoCalificaciones()){
            if(motivo.getFechaHoraBaja()==null){
                dtoMotivoCalificacions.add(DTOMotivoCalificacion.builder()
                        .id(motivo.getId())
                        .nombre(motivo.getNombre())
                        .build());
            }
        }
        dto.setMotivos(dtoMotivoCalificacions);
        return dto;
    }

    @Override
    public void altaTipoCalificacion(DTOTipoCalificacion dto) throws Exception {

        if (tipoCalificacionRepository.existsByNombreVigente(dto.getNombre())) {
            throw new Exception("Ya existe un tipo de calificación con ese nombre.");
        }

        Set<String> nombresMotivos = new HashSet<>();
        for (DTOMotivoCalificacion m : dto.getMotivos()) {
            if (!nombresMotivos.add(m.getNombre().trim().toLowerCase())) {
                throw new Exception("Hay motivos duplicados: " + m.getNombre());
            }
        }

        TipoCalificacion entidad = TipoCalificacion.builder()
                .nombre(dto.getNombre())
                .imagen("")
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        entidad = this.save(entidad);
        entidad.setImagen(guardarImagenBase64(dto.getUrl(), entidad.getId()));
        entidad = this.save(entidad);
        registroSingleton.write("Parametros", "tipo_calificacion", "creacion", "TipoCalificacion de ID " + entidad.getId() + " nombre '"+entidad.getNombre()+"'");

        for (DTOMotivoCalificacion m : dto.getMotivos()) {
            if(m.getNombre().isEmpty())throw new Exception("No pueden guardarse motivos sin nombre");
            MotivoCalificacion nuevoMotivo = MotivoCalificacion.builder()
                    .nombre(m.getNombre())
                    .tipoCalificacion(entidad)
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();

            nuevoMotivo=motivoCalificacionRepository.save(nuevoMotivo);
            registroSingleton.write("Parametros", "motivo_calificacion", "creacion", "MotivoCalificacion de ID " + nuevoMotivo.getId() + " nombre '"+nuevoMotivo.getNombre()+"'");
        }
    }


    @Override
    public void modificarTipoCalificacion(DTOTipoCalificacion dto) throws Exception {

        if (tipoCalificacionRepository.existsByNombreVigenteExcludingId(dto.getNombre(), dto.getId())) {
            throw new Exception("Ya existe otro tipo de calificación con ese nombre.");
        }

        Set<String> nombresEnDto = new HashSet<>();
        for (DTOMotivoCalificacion m : dto.getMotivos()) {
            String norm = m.getNombre().trim().toLowerCase();
            if (!nombresEnDto.add(norm)) {
                throw new Exception("Hay motivos duplicados en la solicitud: " + m.getNombre());
            }
        }

        tipoCalificacionRepository.update(
                dto.getId(),
                guardarImagenBase64(dto.getUrl(), dto.getId()),
                dto.getNombre()
        );

        registroSingleton.write("Parametros", "tipo_calificacion", "modificacion", "TipoCalificacion de ID " + dto.getId() + " nombre '"+dto.getNombre()+"'");

        TipoCalificacion tipo = tipoCalificacionRepository.findById(dto.getId())
                .orElseThrow(() -> new Exception("Tipo no encontrado"));

        List<MotivoCalificacion> actuales =
                motivoCalificacionRepository.findByTipoCalificacionIdAndFechaHoraBajaIsNull(dto.getId());

        Map<Long, MotivoCalificacion> mapaActuales = actuales.stream()
                .collect(Collectors.toMap(MotivoCalificacion::getId, m -> m));

        Set<Long> idsRecibidos = new HashSet<>();

        for (DTOMotivoCalificacion mDto : dto.getMotivos()) {

            if (mDto.getId() != null && mapaActuales.containsKey(mDto.getId())) {
                MotivoCalificacion existente = mapaActuales.get(mDto.getId());
                existente.setNombre(mDto.getNombre());
                motivoCalificacionRepository.save(existente);
                idsRecibidos.add(mDto.getId());
                registroSingleton.write("Parametros", "motivo_calificacion", "modificacion", "MotivoCalificacion de ID " + existente.getId() + " nombre '"+existente.getNombre()+"'");
            }
            else {
                MotivoCalificacion nuevo = MotivoCalificacion.builder()
                        .nombre(mDto.getNombre())
                        .tipoCalificacion(tipo)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
                nuevo=motivoCalificacionRepository.save(nuevo);
                registroSingleton.write("Parametros", "motivo_calificacion", "creacion", "MotivoCalificacion de ID " + nuevo.getId() + " nombre '"+nuevo.getNombre()+"'");
            }
        }

        for (MotivoCalificacion motivo : actuales) {
            if (!idsRecibidos.contains(motivo.getId())) {
                motivo.setFechaHoraBaja(LocalDateTime.now());
                motivoCalificacionRepository.save(motivo);
                registroSingleton.write("Parametros", "motivo_calificacion", "eliminacion", "MotivoCalificacion de ID " + motivo.getId() + " nombre '"+motivo.getNombre()+"'");
            }
        }
    }

    @Override
    public void bajaTipoCalificacion(Long id) throws Exception {
        tipoCalificacionRepository.delete(id, LocalDateTime.now());
        registroSingleton.write("Parametros", "tipo_calificacion", "eliminacion", "TipoCalificacion de ID " + id);
    }

    @Override
    public void restaurarTipoCalificacion(Long id) throws Exception {
        TipoCalificacion tipoCalificacion = tipoCalificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de calificación no encontrado"));
        if (tipoCalificacionRepository.existsByNombreVigenteExcludingId(tipoCalificacion.getNombre(), tipoCalificacion.getId())) {
            throw new Exception("Ya existe otro tipo de calificación con ese nombre.");
        }
        tipoCalificacion.setFechaHoraBaja(null);
        tipoCalificacionRepository.save(tipoCalificacion);
        registroSingleton.write("Parametros", "tipo_calificacion", "restauracion", "TipoCalificacion de ID " + id);
    }

    private String guardarImagenBase64(String dataUrl, Long id) throws IOException {

        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "icono" + id + extension;
        if (!Files.exists(Paths.get(imagenesDirectorio))) {
            Files.createDirectories(Paths.get(imagenesDirectorio));
        }
        Path filePath=Paths.get(imagenesDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return fileName;
    }

    private String encodeFileToBase64(String fileName) {
        try {
            Path filePath = Paths.get(imagenesDirectorio).resolve(fileName).toAbsolutePath().normalize();
            byte[] fileContent = Files.readAllBytes(filePath);

            String contentType;
            if (fileName.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else {
                contentType = "image/png";
            }

            String base64 = Base64.getEncoder().encodeToString(fileContent);
            return "data:" + contentType + ";base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo archivo de imagen: " + fileName, e);
        }
    }
}