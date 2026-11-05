package cz.fel.cvut.attendance.service.model;

import cz.fel.cvut.attendance.service.model.admin.TrainerTrainingInfo;

import java.util.List;

public record TrainerDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        int hourlyRate,
        List<TrainerTrainingInfo> trainings) {
}
