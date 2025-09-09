package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingUnitMapper {
    @Mapping(source = "training.dayOfWeek", target = "dayOfWeek")
    @Mapping(source = "training.school.name", target = "schoolName")
    @Mapping(source = "training.startTime", target = "startTime")
    @Mapping(source = "training.endTime", target = "endTime")
    @Mapping(source = "training.name", target = "name")
    @Mapping(expression = "java(trainingUnitEntity.getTraining().getChildren().size())", target = "numberOfChildren")
    @Mapping(source = "training.school.instructions", target = "instructions")
    @Mapping(source = "training.school.contactPerson", target = "contactPerson")
    @Mapping(source = "training.school.contactNumber", target = "contactNumber")
    TrainingUnitDto toDto(TrainingUnit trainingUnitEntity);
    TrainingUnit toEntity(TrainingUnitDto trainingUnitDto);
    List<TrainingUnitDto> toDtoList(List<TrainingUnit> trainingUnits);
    List<TrainingUnit> toEntityList(List<TrainingUnitDto> trainingUnitDtos);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrainingUnitFromDto(TrainingUnitDto trainingUnitDto, @MappingTarget Training training);
}
