package cz.cvut.fel.attendance.service.api;

import cz.cvut.fel.attendance.service.config.JwtUtil;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.cvut.fel.attendance.service.service.UserService;
import cz.fel.cvut.attendance.service.api.UserApi;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.auth.AuthResponse;
import cz.fel.cvut.attendance.service.model.auth.LoginRequest;
import cz.fel.cvut.attendance.service.model.auth.PasswordChangeRequest;
import cz.fel.cvut.attendance.service.model.auth.RefreshTokenRequestDto;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.TrainerRegisterRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.generateRandomPassword;

@RestController
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<AuthResponse> registerParent(ParentRegisterRequest request) {
        String email = request.email();
        String password = request.password();

        userService.registerParent(request);

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserException("User not found.", HttpStatus.NOT_FOUND));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        final UserDetails userDetails = userService.loadUserByUsername(email);
        final String accessToken = jwtUtil.generateToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @Override
    public ResponseEntity<HttpStatus> registerTrainer(TrainerRegisterRequest request) {
        String email = request.email();
        String password = generateRandomPassword();

        userService.registerTrainer(request, password);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<HttpStatus> changePassword(PasswordChangeRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.changePassword(email, request.currentPassword(), request.newPassword()));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        String email = request.email();
        String password = request.password();
        User user = userService.login(email, password);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        final UserDetails userDetails = userService.loadUserByUsername(email);
        final String accessToken = jwtUtil.generateToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @Override
    public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequestDto request) {
        String email;
        try {
            email = jwtUtil.extractUsername(request.refreshToken());
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token.");
        }

        UserDetails userDetails = userService.loadUserByUsername(email);

        Claims claims = jwtUtil.getClaimsFromRefreshToken(request.refreshToken());

        User user = userRepository.findByEmail(email).orElseThrow(()-> new UserException("User not found.", HttpStatus.NOT_FOUND));

        if (!"refresh".equals(claims.get("type"))) {
            throw new RuntimeException("Invalid token type.");
        }

        if (!jwtUtil.validateToken(request.refreshToken(), userDetails)) {
            throw new RuntimeException("Refresh token expired or invalid.");
        }

        String newAccessToken = jwtUtil.generateToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, user.getRole().toString(), user.getFirstName(), user.getLastName());

        return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
    }
}
