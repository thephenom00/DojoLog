package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;

public record HolidayDto(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate
) {
}
