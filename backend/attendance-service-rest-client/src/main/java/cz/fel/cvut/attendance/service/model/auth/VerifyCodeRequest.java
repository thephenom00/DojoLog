package cz.fel.cvut.attendance.service.model.auth;

public record VerifyCodeRequest(
  String email,
  String code
) {
}
