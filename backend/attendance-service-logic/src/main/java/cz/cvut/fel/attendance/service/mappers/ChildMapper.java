package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.School;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.SchoolDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildMapper {
    ChildDto toDto(Child childEntity);

    Child toEntity(ChildDto childDto);

    List<ChildDto> toDtoList(List<Child> children);

    List<Child> toEntityList(List<ChildDto> childDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChildFromDto(ChildDto childDto, @MappingTarget Child Child);
}
