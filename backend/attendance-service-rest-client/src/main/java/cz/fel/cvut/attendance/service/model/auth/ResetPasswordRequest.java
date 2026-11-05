package cz.fel.cvut.attendance.service.model.auth;

public record ResetPasswordRequest(
  String password,
  String token
) {
}
