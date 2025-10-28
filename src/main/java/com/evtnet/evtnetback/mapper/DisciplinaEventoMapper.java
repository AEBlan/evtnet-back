package com.evtnet.evtnetback.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    builder = @Builder(disableBuilder = true) // ⬅️ evitar usar Lombok Builder
)
public interface DisciplinaEventoMapper {
 /* *
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
    }*/
}
