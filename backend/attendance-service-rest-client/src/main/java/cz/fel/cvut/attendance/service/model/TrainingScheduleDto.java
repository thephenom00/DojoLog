package cz.fel.cvut.attendance.service.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TrainingScheduleDto(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
