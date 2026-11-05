package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.Holiday;
import cz.fel.cvut.attendance.service.model.HolidayDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HolidayMapper {
    HolidayDto toDto(Holiday holidayEntity);
    Holiday toEntity(HolidayDto holidayDto);

    List<HolidayDto> toDtoList(List<Holiday> holidayList);
}
