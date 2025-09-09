package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.TrainerSubstitution;
import cz.fel.cvut.attendance.service.model.TrainerSubstitutionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerSubstitutionMapper {
    TrainerSubstitutionDto toDto(TrainerSubstitution trainerSubstitutionEntity);
}
