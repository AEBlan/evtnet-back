package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ImagenMascota;
import com.evtnet.evtnetback.repository.BaseRepository;

public class ImagenMascotaServiceImpl extends BaseServiceImpl <ImagenMascota, Long> implements ImagenMascotaService {

    public ImagenMascotaServiceImpl(BaseRepository<ImagenMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
