package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Caracteristica;
import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import com.evtnet.evtnetback.Repositories.IconoCaracteristicaRepository;
import com.evtnet.evtnetback.Repositories.CaracteristicaRepository;
import com.evtnet.evtnetback.dto.iconos.DTOIconoCaracteristica;   // <-- DTO
import com.evtnet.evtnetback.Services.UploadsService;            // <-- paquete lowercase
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class IconoCaracteristicaServiceImpl extends BaseServiceImpl<IconoCaracteristica, Long>
        implements IconoCaracteristicaService {

    private final IconoCaracteristicaRepository iconoRepo;
    private final CaracteristicaRepository carRepo;
    private final UploadsService uploads;

    public IconoCaracteristicaServiceImpl(IconoCaracteristicaRepository iconoRepo,
                                          CaracteristicaRepository carRepo,
                                          UploadsService uploads) {
        super(iconoRepo);
        this.iconoRepo = iconoRepo;
        this.carRepo = carRepo;
        this.uploads = uploads;
    }

    @Override
    @Transactional
    public DTOIconoCaracteristica subirIcono(Long caracteristicaId, MultipartFile file) {
        Caracteristica car = carRepo.findById(caracteristicaId)
                .orElseThrow(() -> new IllegalArgumentException("Característica inexistente"));

        String url = uploads.savePngOrSvg(file, "iconos");

        // si preferís sin Lombok.builder() para evitar dependencia:
        IconoCaracteristica icono = new IconoCaracteristica();
        icono.setImagen(url);
        icono.setFechaHoraAlta(LocalDateTime.now());
        icono = iconoRepo.save(icono);

        car.setIconoCaracteristica(icono);
        carRepo.save(car);

        return new DTOIconoCaracteristica(
        icono.getId(),
        icono.getImagen(),
        icono.getFechaHoraAlta().toString()
        );
    }

    @Override
    @Transactional
    public void eliminarIcono(Long iconoId) {
        IconoCaracteristica ic = iconoRepo.findById(iconoId)
                .orElseThrow(() -> new IllegalArgumentException("Ícono no existe"));

        uploads.deleteByPublicUrl(ic.getImagen());
        iconoRepo.delete(ic);
    }
}
