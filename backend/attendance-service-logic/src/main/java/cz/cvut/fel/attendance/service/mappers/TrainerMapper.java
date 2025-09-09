package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Trainer;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerDto toDto(Trainer trainerEntity);
    List<TrainerDto> toDtoList(List<Trainer> trainerDtos);
}
