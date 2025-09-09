package cz.fel.cvut.attendance.service.model;

public record TrainerAttendanceDto(
        Long id,
        String firstName,
        String lastName,
        boolean present
) {
}
