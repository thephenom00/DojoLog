package cz.fel.cvut.attendance.service.model.auth;

public record PasswordChangeRequest(
        String currentPassword,
        String newPassword
) {
}
