package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Parent;
import cz.fel.cvut.attendance.service.model.ParentDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ParentMapper {
    ParentDto toDto(Parent parentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateParentFromDto(ParentDto parentDto, @MappingTarget Parent parent);
}
