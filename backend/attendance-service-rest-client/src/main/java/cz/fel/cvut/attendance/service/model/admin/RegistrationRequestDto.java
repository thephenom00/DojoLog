package cz.fel.cvut.attendance.service.model.admin;

import java.time.LocalDate;

public record RegistrationRequestDto(
        Long childId,
        String firstName,
        String lastName,
        String parentFirstName,
        String parentLastName,
        String parentEmail,
        String parentPhoneNumber,
        String schoolName,
        String trainingDayOfWeek,
        String trainingName,
        String trainingTime,
        String birthNumber,
        LocalDate dateOfBirth,
        String city,
        String street,
        int zip
) {
}
