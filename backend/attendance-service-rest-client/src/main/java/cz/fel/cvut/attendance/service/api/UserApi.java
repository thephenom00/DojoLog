package cz.fel.cvut.attendance.service.api;

import cz.fel.cvut.attendance.service.model.auth.AuthResponse;
import cz.fel.cvut.attendance.service.model.auth.LoginRequest;
import cz.fel.cvut.attendance.service.model.auth.PasswordChangeRequest;
import cz.fel.cvut.attendance.service.model.auth.RefreshTokenRequestDto;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.TrainerRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/auth")
public interface UserApi {

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
    ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequestDto request);


}
