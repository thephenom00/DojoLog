package cz.fel.cvut.attendance.service.model.admin;

import java.time.LocalDate;

public record ChildParentInfo(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String street,
        String city,
        int zip,
        String birthNumber,
        boolean paid,
        String parentName,
        String email,
        String phoneNumber,
        String schoolName
) {
}
