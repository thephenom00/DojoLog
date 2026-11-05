package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.auth.AuthResponse;
import cz.fel.cvut.attendance.service.model.auth.EmailRequest;
import cz.fel.cvut.attendance.service.model.auth.LoginRequest;
import cz.fel.cvut.attendance.service.model.auth.PasswordChangeRequest;
import cz.fel.cvut.attendance.service.model.auth.RefreshTokenRequest;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.ResetPasswordRequest;
import cz.fel.cvut.attendance.service.model.auth.ResetTokenResponse;
import cz.fel.cvut.attendance.service.model.auth.TrainerRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.VerifyCodeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/auth")
public interface UserApi {

    @PostMapping("/generate-otp")
    @ResponseBody
    ResponseEntity<HttpStatus> generateOtp(@RequestBody EmailRequest request);

    @PostMapping("/check-otp")
    @ResponseBody
    ResponseEntity<ResetTokenResponse> checkOtp(@RequestBody VerifyCodeRequest request);

    @PostMapping("/reset-password")
    ResponseEntity<HttpStatus> resetPassword(@RequestBody ResetPasswordRequest request);

    @PostMapping("/register/parent")
    @ResponseBody
    ResponseEntity<AuthResponse> registerParent(@RequestBody ParentRegisterRequest request);

    @PostMapping("/register/trainer")
    @ResponseBody
    ResponseEntity<HttpStatus> registerTrainer(@RequestBody TrainerRegisterRequest request);

    @PostMapping("/change-password")
    @ResponseBody
    ResponseEntity<HttpStatus> changePassword(@RequestBody PasswordChangeRequest request);

    @PostMapping("/login")
    @ResponseBody
    ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request);

    @PostMapping("/refresh")
    @ResponseBody
    ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request);

    @PostMapping("/register/otp/generate")
    @ResponseBody
    ResponseEntity<HttpStatus> generateRegistrationOtp(@RequestBody EmailRequest request);
}
