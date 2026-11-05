package cz.fel.cvut.attendance.service.model.trainer;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TrainerReportItemDto(
        Long id,
        String date,
        String name,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String category,
        int money,
        boolean manual,
        boolean trainingAttendance,
        Long sourceId
) {
}
