package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Training;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    TrainingDto toDto(Training trainingEntity);
    Training toEntity(TrainingDto trainingDto);
    List<TrainingDto> toDtoList(List<Training> trainings);
    List<Training> toEntityList(List<TrainingDto> trainingDtos);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrainingFromDto(TrainingDto trainingDto, @MappingTarget Training training);
}
