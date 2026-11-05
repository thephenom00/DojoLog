package cz.cvut.fel.attendance.service.mappers;

import cz.fel.cvut.attendance.service.model.SchoolDto;
import cz.cvut.fel.attendance.service.model.School;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
    SchoolDto toDto(School schoolEntity);

    School toEntity(SchoolDto schoolDto);

    List<SchoolDto> toDtoList(List<School> schools);
    List<School> toEntityList(List<SchoolDto> schoolDtos);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSchoolFromDto(SchoolDto schoolDto, @MappingTarget School school);

}
