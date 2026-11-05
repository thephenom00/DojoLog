package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.ChildTrainingAttendance;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChildAttendanceMapper {
    @Mapping(source = "child.firstName", target = "firstName")
    @Mapping(source = "child.lastName", target = "lastName")
    ChildAttendanceDto toDto(ChildTrainingAttendance childTrainingAttendanceEntity);

    ChildTrainingAttendance toEntity(ChildAttendanceDto childAttendanceDto);

    List<ChildAttendanceDto> toDtoList(List<ChildTrainingAttendance> childTrainingAttendances);
    List<ChildTrainingAttendance> toEntityList(List<ChildAttendanceDto> childAttendanceDtos);
   }
