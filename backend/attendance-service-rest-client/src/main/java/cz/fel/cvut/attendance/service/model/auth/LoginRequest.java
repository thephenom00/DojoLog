package cz.fel.cvut.attendance.service.model.auth;

public record LoginRequest(
        String email,
        String password
) {
}
