package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerAttendanceMapper {
    @Mapping(source = "trainer.firstName", target = "firstName")
    @Mapping(source = "trainer.lastName", target = "lastName")
    TrainerAttendanceDto toDto(TrainerAttendance trainerAttendanceEntity);
    TrainerAttendance toEntity(TrainerAttendanceDto trainerAttendanceDto);
    List<TrainerAttendanceDto> toDtoList(List<TrainerAttendance> trainerAttendances);
    List<TrainerAttendance> toEntityList(List<TrainerAttendanceDto> trainerAttendanceDtos);
}
