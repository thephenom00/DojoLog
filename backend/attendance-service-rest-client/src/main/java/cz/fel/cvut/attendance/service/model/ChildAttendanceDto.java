package cz.fel.cvut.attendance.service.model;

public record ChildAttendanceDto(
        Long id,
        String firstName,
        String lastName,
        boolean present
) {
}
