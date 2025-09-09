package cz.fel.cvut.attendance.service.model.trainer;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TrainerReportDto(
        String date,
        String name,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        double hours,
        String type,
        int money
) {
}
