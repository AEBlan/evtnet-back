package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ImagenEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ImagenEspacioServiceImpl extends BaseServiceImpl <ImagenEspacio, Long> implements ImagenEspacioService {

    public ImagenEspacioServiceImpl(BaseRepository<ImagenEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
