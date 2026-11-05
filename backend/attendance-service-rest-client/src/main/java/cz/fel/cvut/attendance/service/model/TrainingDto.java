package cz.fel.cvut.attendance.service.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record TrainingDto(
        Long id,
        String name,
        int price,
        int capacity,
        int initialCapacity,
        List<TrainingScheduleDto> trainingSchedules
) {}
