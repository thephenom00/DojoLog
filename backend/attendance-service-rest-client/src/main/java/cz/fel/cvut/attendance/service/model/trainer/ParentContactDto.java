package cz.fel.cvut.attendance.service.model.trainer;


public record ParentContactDto(
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}
