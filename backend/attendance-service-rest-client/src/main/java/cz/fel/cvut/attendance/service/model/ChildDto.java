package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;

public record ChildDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String street,
        String city,
        int zip,
        String birthNumber,
        Long requestedTrainingId
) {}
