package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
        String eventType,
        LocalDate deadline,
        List<String> trainerEmails,
        List<String> trainerNames,
        List<String> trainerPhones
){}
