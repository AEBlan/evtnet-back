package com.evtnet.evtnetback.mapper;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.dto.eventos.DTOEventoCreate;
import com.evtnet.evtnetback.dto.eventos.DTOEventoResponse;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = { DisciplinaEventoMapper.class },
    builder = @Builder(disableBuilder = true) 
)
public interface EventoMapper {

    // ----------- Entity <- Create DTO -----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "superEvento", ignore = true)
    @Mapping(target = "tipoInscripcionEvento", ignore = true)
    @Mapping(target = "modoEvento", ignore = true)
    // @Mapping(target = "administradorEvento", ignore = true) // si aplica
    @Mapping(target = "espacio", ignore = true)
    @Mapping(target = "disciplinasEvento", ignore = true)
    @Mapping(target = "inscripciones", ignore = true)
    @Mapping(target = "eventosModoEvento", ignore = true)
    @Mapping(target = "porcentajesReintegroCancelacion", ignore = true)
    @Mapping(target = "denunciasEvento", ignore = true)
    @Mapping(target = "fechaBaja", ignore = true)
    Evento toEntity(DTOEventoCreate dto);

    // ----------- Response DTO <- Entity -----------
    @Mapping(target = "superEventoId", source = "superEvento.id")
    @Mapping(target = "tipoInscripcionEventoId", source = "tipoInscripcionEvento.id")
    @Mapping(target = "modoEventoId", source = "modoEvento.id")
    // Si tu DTO lo tiene y tu entidad también:
    // @Mapping(target = "administradorEventoId", source = "administradorEvento.id")
    @Mapping(target = "espacioId", source = "espacio.id")
    DTOEventoResponse toResponse(Evento entity);
}
