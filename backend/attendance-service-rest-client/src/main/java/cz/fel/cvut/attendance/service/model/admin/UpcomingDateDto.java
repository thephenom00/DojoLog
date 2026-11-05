package cz.fel.cvut.attendance.service.model.admin;

import java.time.LocalDate;

public record UpcomingDateDto(
        Long id,
        LocalDate date,
        Long scheduleId
) {

}
