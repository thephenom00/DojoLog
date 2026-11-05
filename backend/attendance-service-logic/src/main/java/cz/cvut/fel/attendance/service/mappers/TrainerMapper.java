package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.Training;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.admin.TrainerTrainingInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerDto toDto(Trainer trainerEntity);
    List<TrainerDto> toDtoList(List<Trainer> trainerDtos);

    @Mapping(source = "school.name", target = "schoolName")
    TrainerTrainingInfo toTrainerTrainingInfo(Training training);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    void updateTrainerFromDto(TrainerDto trainerDto, @MappingTarget Trainer trainer);
}
