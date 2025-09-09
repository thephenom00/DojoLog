package cz.fel.cvut.attendance.service.model.parent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ChildUpcomingTrainingUnitDto(
        Long id,
        LocalDate date,
        DayOfWeek dayOfWeek,
        String name,
        String schoolName,
        LocalTime startTime,
        LocalTime endTime,

        List<String> childNames,

        List<String> trainerNames,
        List<String> trainerPhoneNumbers
) {
}
