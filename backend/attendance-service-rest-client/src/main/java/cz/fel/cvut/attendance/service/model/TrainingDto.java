package cz.fel.cvut.attendance.service.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record TrainingDto(
        Long id,
        String name,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int price,
        int capacity
) {}
