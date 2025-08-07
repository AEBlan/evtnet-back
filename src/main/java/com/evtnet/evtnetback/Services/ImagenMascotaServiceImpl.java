package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ImagenMascota;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ImagenMascotaServiceImpl extends BaseServiceImpl <ImagenMascota, Long> implements ImagenMascotaService {

    public ImagenMascotaServiceImpl(BaseRepository<ImagenMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
