package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventDto (
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        String location,
        LocalTime startTime,
        LocalTime endTime,
        int places,
        int takenPlaces,
        int price,
        String description,
        int trainerSalary,
        String eventType
){}
