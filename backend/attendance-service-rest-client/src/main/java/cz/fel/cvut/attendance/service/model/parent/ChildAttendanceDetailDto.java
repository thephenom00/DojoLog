package cz.fel.cvut.attendance.service.model.parent;

import java.time.LocalDate;

public record ChildAttendanceDetailDto(
        LocalDate date,
        String school,
        String description
) {
}
