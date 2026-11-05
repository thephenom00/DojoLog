package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;

public record NewsDto(
        Long id,
        String name,
        LocalDate date,
        String description
) {
}
