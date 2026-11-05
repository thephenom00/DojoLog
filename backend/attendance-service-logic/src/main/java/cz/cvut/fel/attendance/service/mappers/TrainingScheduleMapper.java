package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.News;
import cz.cvut.fel.attendance.service.model.TrainingSchedule;
import cz.fel.cvut.attendance.service.model.NewsDto;
import cz.fel.cvut.attendance.service.model.TrainingScheduleDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingScheduleMapper {
    TrainingScheduleDto toDto(TrainingSchedule trainingScheduleEntity);
    TrainingSchedule toEntity(TrainingScheduleDto trainingScheduleDto);
    List<TrainingSchedule> toDtoList (List<TrainingScheduleDto> trainingScheduleDtos);
}
