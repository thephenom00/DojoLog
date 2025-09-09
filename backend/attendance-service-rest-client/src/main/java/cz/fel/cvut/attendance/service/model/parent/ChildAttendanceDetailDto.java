package cz.fel.cvut.attendance.service.model.parent;

public record ChildAttendanceDetailDto(
        String date,
        String school,
        String description
) {
}
