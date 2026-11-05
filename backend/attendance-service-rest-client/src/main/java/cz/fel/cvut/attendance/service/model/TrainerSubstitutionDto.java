package cz.fel.cvut.attendance.service.model;

import java.time.LocalDate;

public record TrainerSubstitutionDto(
        Long id,
        Long originalTrainerId,
        Long substituteTrainerId,
        LocalDate date
) {}
