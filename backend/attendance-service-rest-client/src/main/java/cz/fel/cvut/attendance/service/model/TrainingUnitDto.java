package cz.fel.cvut.attendance.service.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record TrainingUnitDto(
        Long id,
        LocalDate date,
        String description,
        DayOfWeek dayOfWeek,
        String name,
        String schoolName,
        LocalTime startTime,
        LocalTime endTime,
        int numberOfChildren,
        String instructions,
        String contactPerson,
        String contactNumber
) {
}
