package cz.fel.cvut.attendance.service.model.admin;

public record AppSettingsDto(
        boolean enrollmentOpen,
        boolean trainingGeneration
) {}
