package cz.fel.cvut.attendance.service.model;

public record ParentDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
