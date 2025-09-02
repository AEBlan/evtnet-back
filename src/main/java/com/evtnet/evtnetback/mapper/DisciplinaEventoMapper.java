package com.evtnet.evtnetback.mapper;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Entities.DisciplinaEvento;
import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoResponse;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true) // ⬅️ evitar usar Lombok Builder
)
public interface DisciplinaEventoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evento", ignore = true)
    @Mapping(target = "disciplina", ignore = true)
    @Mapping(target = "fechaBaja", ignore = true)
    DisciplinaEvento toEntity(DTODisciplinaEventoCreate dto);

    @Mapping(target = "disciplina", source = "disciplina", qualifiedByName = "disciplinaToRef")
    DTODisciplinaEventoResponse toResponse(DisciplinaEvento entity);

    @Named("disciplinaToRef")
    default DTODisciplinaRef disciplinaToRef(Disciplina d) {
        if (d == null) return null;
        DTODisciplinaRef ref = new DTODisciplinaRef();
        ref.setId(d.getId());
        ref.setNombre(d.getNombre());
        return ref;
    }
}
