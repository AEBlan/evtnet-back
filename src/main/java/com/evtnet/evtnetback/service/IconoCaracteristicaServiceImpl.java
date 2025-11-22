package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Caracteristica;
import com.evtnet.evtnetback.entity.Disciplina;
import com.evtnet.evtnetback.entity.IconoCaracteristica;
import com.evtnet.evtnetback.repository.IconoCaracteristicaRepository;
import com.evtnet.evtnetback.repository.CaracteristicaRepository;
import com.evtnet.evtnetback.dto.espacios.DTOCaracteristicaSubEspacio;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import com.evtnet.evtnetback.util.RegistroSingleton;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class IconoCaracteristicaServiceImpl extends BaseServiceImpl<IconoCaracteristica, Long>
        implements IconoCaracteristicaService {

    @Value("${app.storage.iconos:/app/storage/iconos}")
    private String iconosDirectorio;

    private final IconoCaracteristicaRepository iconoCaracteristicaRepository;
    private final CaracteristicaRepository caracteristicaRepository;
    private final UploadsService uploads;
    private final ParametroSistemaService parametroSistemaService;
    private final RegistroSingleton registroSingleton;

    public IconoCaracteristicaServiceImpl(IconoCaracteristicaRepository iconoCaracteristicaRepository,
                                          CaracteristicaRepository caracteristicaRepository,
                                          UploadsService uploads,
                                          ParametroSistemaService parametroSistemaService,
                                          RegistroSingleton registroSingleton) {
        super(iconoCaracteristicaRepository);
        this.iconoCaracteristicaRepository = iconoCaracteristicaRepository;
        this.caracteristicaRepository = caracteristicaRepository;
        this.uploads = uploads;
        this.parametroSistemaService = parametroSistemaService;
        this.registroSingleton = registroSingleton;
    }

    @Override
    public Page<DTOIconoCaracteristica> obtenerListaIconoCaracteristica(int page, boolean vigentes, boolean dadasDeBaja) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHoraBaja"),
                        Sort.Order.asc("fechaHoraAlta")
                )
        );

        Specification<IconoCaracteristica> spec = Specification.where(null);
        boolean vigentesFiltro = Boolean.TRUE.equals(vigentes);
        boolean dadasDeBajaFiltro = Boolean.TRUE.equals(dadasDeBaja);

        if (vigentesFiltro && !dadasDeBajaFiltro) {
            spec = spec.and((root, cq, cb) -> cb.isNull(root.get("fechaHoraBaja")));
        }

        if (!vigentesFiltro && dadasDeBajaFiltro) {
            spec = spec.and((root, cq, cb) -> cb.isNotNull(root.get("fechaHoraBaja")));
        }
        Page<IconoCaracteristica> iconosCaracteristica = iconoCaracteristicaRepository.findAll(spec, pageable);

        List<IconoCaracteristica> filtrados = iconosCaracteristica
                .stream()
                .filter(ic -> ic.getImagen() != null && Files.exists(Paths.get(ic.getImagen())))
                .toList();

        List<DTOIconoCaracteristica> dtos = filtrados.stream().map(ic -> {
            String base64Image = encodeFileToBase64(ic.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];

            String extension = "";
            Path path = Paths.get(ic.getImagen());
            String fileName = path.getFileName().toString();

            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
                extension = fileName.substring(dotIndex + 1).toLowerCase();
            }

            String contentType = extension.equals("svg") ? "svg" : "png";

            return DTOIconoCaracteristica.builder()
                    .id(ic.getId())
                    .url(base64Data)
                    .fechaAlta(ic.getFechaHoraAlta() == null ? null :
                            ic.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .fechaBaja(ic.getFechaHoraBaja() == null ? null :
                            ic.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                    .contentType(contentType)
                    .build();
        }).toList();


        return new PageImpl<>(dtos, pageable, dtos.size());
    }


    @Override
    public DTOIconoCaracteristica obtenerIconoCaracteristicaCompleto(Long id) throws Exception {
        IconoCaracteristica iconoCaracteristica = iconoCaracteristicaRepository.findById(id)
                .orElseThrow(() -> new Exception("Icono no encontrado"));

        String base64Image = encodeFileToBase64(iconoCaracteristica.getImagen());
        String[] parts = base64Image.split(",");
        String base64Data = parts[1];

        String extension = "";
        Path path = Paths.get(iconoCaracteristica.getImagen());
        String fileName = path.getFileName().toString();

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }
        String contentType = extension.equals("svg") ? "svg" : "png";

        return DTOIconoCaracteristica.builder()
                .id(iconoCaracteristica.getId())
                .url(base64Data)
                .contentType(contentType)
                .build();
    }


    @Override
    public void altaIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception {
        IconoCaracteristica icono = this.save(IconoCaracteristica.builder()
                .imagen("")
                .fechaHoraAlta(LocalDateTime.now())
                .build());

        icono.setImagen(guardarImagenBase64(iconoCaracteristica.getUrl(), icono.getId()));
        icono=this.save(icono);
        registroSingleton.write("Parametros", "icono_caracteristica", "creacion", "Icono de ID " + icono.getId());
    }

    @Override
    public void modificarIconoCaracteristica(DTOIconoCaracteristica iconoCaracteristica) throws Exception {
        iconoCaracteristicaRepository.update(iconoCaracteristica.getId(), guardarImagenBase64(iconoCaracteristica.getUrl(), iconoCaracteristica.getId()));
        registroSingleton.write("Parametros", "icono_caracteristica", "modificacion", "Icono de ID " + iconoCaracteristica.getId());
    }

    @Override
    public void bajaIconoCaracteristica(Long id) throws Exception {
        iconoCaracteristicaRepository.delete(id, LocalDateTime.now());
        registroSingleton.write("Parametros", "icono_caracteristica", "eliminacion", "Icono de ID " + id);
    }

    @Override
    public void restaurarIconoCaracteristica(Long id) throws Exception{
        IconoCaracteristica iconoCaracteristica = iconoCaracteristicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ícono de característica no encontrado"));
        iconoCaracteristica.setFechaHoraBaja(null);
        iconoCaracteristica.setFechaHoraAlta(LocalDateTime.now());
        this.save(iconoCaracteristica);
        registroSingleton.write("Parametros", "icono_caracteristica", "restauracion", "Icono de ID " + id);
    }

    @Override
    public IconoCaracteristica obtenerIconosEspacio(Long idIcono)throws Exception {
        return iconoCaracteristicaRepository.findById(idIcono)
                .orElseThrow(() -> new Exception("No se encontró el ícono con ID " + idIcono));

    }

    @Override
    public List<DTOCaracteristicaSubEspacio> obtenerCaracteristicasSubEspacio(Long idEspacio) throws Exception{
        List<Caracteristica>caracteristicas=this.caracteristicaRepository.findBySubEspacio(idEspacio);
        List<DTOCaracteristicaSubEspacio> caracteristicaSubEspacios=new ArrayList<>();
        for(Caracteristica caracteristica:caracteristicas){
            String base64Image = encodeFileToBase64(caracteristica.getIconoCaracteristica().getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            caracteristicaSubEspacios.add(DTOCaracteristicaSubEspacio.builder()
                            .id(caracteristica.getId())
                            .idEspacio(idEspacio)
                            .idIconoCaracteristica(caracteristica.getIconoCaracteristica().getId())
                            .nombre(caracteristica.getNombre())
                            .contentType(contentType)
                            .urlIcono(base64Data)
                    .build()
            );
        }
        return caracteristicaSubEspacios;
    }
    @Override
    public List<DTOIconoCaracteristica> obtenerListaIcono() throws Exception{
        List<IconoCaracteristica> iconosCaracteristica = iconoCaracteristicaRepository.findAll();
        List<DTOIconoCaracteristica> dtoIconoCaracteristicas = new ArrayList<>();
        for (IconoCaracteristica icono:iconosCaracteristica){
            String base64Image = encodeFileToBase64(icono.getImagen());
            String[] parts = base64Image.split(",");
            String base64Data = parts[1];
            String mimeType = parts[0].split(";")[0].split(":")[1];
            String contentType = mimeType.equals("image/svg+xml") ? "svg" : "png";
            dtoIconoCaracteristicas.add(DTOIconoCaracteristica.builder()
                    .id(icono.getId())
                    .url(base64Data)
                    .contentType(contentType)
                    .build());
        }
        return dtoIconoCaracteristicas;
    }

    private String guardarImagenBase64(String dataUrl, Long id) throws IOException {

        String[] parts = dataUrl.split(",");
        String base64Data = parts[1];
        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
        String mimeType = parts[0].split(";")[0].split(":")[1];
        String extension = mimeType.equals("image/svg+xml") ? ".svg" : ".png";
        String fileName = "caracteristica" + id + extension;
        if (!Files.exists(Paths.get(iconosDirectorio))) {
            Files.createDirectories(Paths.get(iconosDirectorio));
        }
        Path filePath=Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
        Files.write(filePath, fileBytes);
        return filePath.toString();
    }

    private String encodeFileToBase64(String fileName) {
        try {
            Path filePath = Paths.get(iconosDirectorio).resolve(fileName).toAbsolutePath().normalize();
            byte[] fileContent = Files.readAllBytes(filePath);

            String contentType;
            if (filePath.endsWith(".svg")) {
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
