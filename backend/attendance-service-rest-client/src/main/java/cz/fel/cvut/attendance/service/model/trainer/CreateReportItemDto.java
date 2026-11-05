package cz.fel.cvut.attendance.service.model.trainer;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateReportItemDto(
        LocalDate date,
        LocalDate endDate,
        String name,
        String category,
        int money,
        LocalTime startTime,
        LocalTime endTime
) {
}
