package cz.cvut.fel.attendance.service.mappers;

import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainerReportMapper {
    TrainerReportDto toDto(TrainerReport trainerReportEntity);
    List<TrainerReportDto> toDtoList(List<TrainerReport> trainerReports);

}
