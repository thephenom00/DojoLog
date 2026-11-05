package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.config.JwtUtil;
import cz.cvut.fel.attendance.service.enums.OtpPurpose;
import cz.cvut.fel.attendance.service.model.*;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.cvut.fel.attendance.service.repository.VerificationCodeRepository;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.auth.EmailRequest;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.ResetPasswordRequest;
import cz.fel.cvut.attendance.service.model.auth.ResetTokenResponse;
import cz.fel.cvut.attendance.service.model.auth.TrainerRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.VerifyCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingUnitService trainingUnitService;

    private final VerificationCodeRepository verificationCodeRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    private final SecureRandom secureRandom = new SecureRandom();

    private final JwtUtil jwtUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Transactional
    public HttpStatus resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(jwtUtil.getEmailFromResetToken(request.token()))
                .orElseThrow(() -> new UserException("Invalid token", HttpStatus.BAD_REQUEST));

        if (!jwtUtil.validateResetToken(request.token())) {
            throw new UserException("Token has expired", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        verificationCodeRepository.deleteByEmailAndPurpose(user.getEmail(), OtpPurpose.FORGOTTEN_PASSWORD);

        return HttpStatus.OK;
    }

    private String normalizeEmail(String email) {
        return email != null ? email.trim().toLowerCase() : null;
    }

    public ResetTokenResponse checkOtp(VerifyCodeRequest request) {
        String email = normalizeEmail(request.email());
        userRepository.findByEmail(email).orElseThrow(
                () -> new UserException("User was not found", HttpStatus.NOT_FOUND)
        );

        VerificationCode vc = verificationCodeRepository
                .findByEmailAndPurpose(email, OtpPurpose.FORGOTTEN_PASSWORD)
                .orElseThrow(() -> new UserException("Code is not correct", HttpStatus.BAD_REQUEST));

        if (!vc.getCode().equals(request.code())) {
            throw new UserException("Code is not correct", HttpStatus.BAD_REQUEST);
        }

        if (vc.getCreatedDate().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new UserException("Verification code has expired", HttpStatus.BAD_REQUEST);
        }

        final String resetToken = jwtUtil.generateResetToken(email);
        return new ResetTokenResponse(resetToken);
    }

    @Transactional
    public HttpStatus generateOtp(EmailRequest emailRequest) {
        String email = normalizeEmail(emailRequest.email());
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UserException("User was not found", HttpStatus.NOT_FOUND)
        );

        // Rate-limit: 1 request per minute
        Optional<VerificationCode> existing = verificationCodeRepository
                .findByEmailAndPurpose(email, OtpPurpose.FORGOTTEN_PASSWORD);

        if (existing.isPresent()) {
            if (LocalDateTime.now().isBefore(existing.get().getCreatedDate().plusMinutes(1))) {
                throw new UserException("Too many requests was sent", HttpStatus.TOO_MANY_REQUESTS);
            }
            verificationCodeRepository.deleteByEmailAndPurpose(email, OtpPurpose.FORGOTTEN_PASSWORD);
        }

        String codeString = generateSixDigitCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(codeString);
        verificationCode.setCreatedDate(LocalDateTime.now());
        verificationCode.setPurpose(OtpPurpose.FORGOTTEN_PASSWORD);
        verificationCodeRepository.save(verificationCode);

        mailService.sendPasswordResetMail(user.getEmail(), codeString);

        return HttpStatus.OK;
    }

    @Transactional
    public HttpStatus generateRegistrationOtp(EmailRequest emailRequest) {
        String email = normalizeEmail(emailRequest.email());
        // Email must NOT exist yet — this is for new accounts
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(
                    "User with email: " + email + " already exists.",
                    HttpStatus.CONFLICT
            );
        }

        Optional<VerificationCode> existing = verificationCodeRepository
                .findByEmailAndPurpose(email, OtpPurpose.REGISTER);

        if (existing.isPresent()) {
            if (LocalDateTime.now().isBefore(existing.get().getCreatedDate().plusMinutes(1))) {
                throw new UserException("Too many requests was sent", HttpStatus.TOO_MANY_REQUESTS);
            }
            verificationCodeRepository.deleteByEmailAndPurpose(email, OtpPurpose.REGISTER);
        }

        String codeString = generateSixDigitCode();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(codeString);
        verificationCode.setCreatedDate(LocalDateTime.now());
        verificationCode.setPurpose(OtpPurpose.REGISTER);
        verificationCodeRepository.save(verificationCode);

        mailService.sendRegistrationOtpMail(email, codeString);

        return HttpStatus.OK;
    }

    @Transactional
    public void registerParent(ParentRegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException(
                    "User with email: " + email + " already exists.",
                    HttpStatus.CONFLICT
            );
        }

        // Verify the OTP before doing anything else
        VerificationCode vc = verificationCodeRepository
                .findByEmailAndPurpose(email, OtpPurpose.REGISTER)
                .orElseThrow(() -> new UserException(
                        "Verification code not found. Please request a new one.",
                        HttpStatus.BAD_REQUEST
                ));

        if (!vc.getCode().equals(request.otpCode())) {
            throw new UserException("Invalid verification code.", HttpStatus.BAD_REQUEST);
        }

        if (vc.getCreatedDate().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new UserException("Verification code has expired.", HttpStatus.BAD_REQUEST);
        }

        Parent parent = new Parent(
                request.firstName(),
                request.lastName(),
                email,
                request.phoneNumber(),
                passwordEncoder.encode(request.password())
        );

        try {
            userRepository.save(parent);
            // Clean up the OTP row after successful registration
            verificationCodeRepository.deleteByEmailAndPurpose(email, OtpPurpose.REGISTER);
            mailService.sendNewParentNotificationMail(request, adminEmail);
            mailService.sendParentRegistrationMail(parent);
        } catch (Exception e) {
            throw new UserException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public void registerTrainer(TrainerRegisterRequest request, String password) {
        String email = normalizeEmail(request.email());
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserException("User with email: " + email + " already exists.", HttpStatus.CONFLICT);
        } else if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new UserException("User with phone number: " + request.phoneNumber() + " already exists.", HttpStatus.CONFLICT);
        }

        Trainer trainer = new Trainer(
                request.firstName(),
                request.lastName(),
                email,
                request.phoneNumber(),
                passwordEncoder.encode(password),
                request.hourlyRate()
        );

        Training training = null;

        if (request.trainingId() != null) {
            training = trainingRepository.findById(request.trainingId()).orElseThrow(() ->
                    new TrainingException("Training with ID: " + request.trainingId() + " not found.", HttpStatus.NOT_FOUND));
            training.addTrainer(trainer);
            trainer.addTraining(training);
        }

        try {
            userRepository.save(trainer);
            if (training != null) {
                trainingRepository.save(training);
                trainingUnitService.createAttendanceForCurrentUnits(trainer, training);
            }
            mailService.sendTrainerRegistrationMail(password, trainer, training);
        } catch (Exception e) {
            throw new UserException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // -------------------------------------------------------------------------
    // PASSWORD CHANGE
    // -------------------------------------------------------------------------

    @Transactional
    public HttpStatus changePassword(String email, String currentPassword, String newPassword) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UserException("User not found.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return HttpStatus.UNAUTHORIZED;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return HttpStatus.OK;
    }


    public User login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UserException("User with email: " + normalizedEmail + " not found.", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        throw new UserException("Invalid credentials. Please check your email and password.", HttpStatus.UNAUTHORIZED);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(email);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        List<GrantedAuthority> authorities = List.of(() -> user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }

    private String generateSixDigitCode() {
        int code = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(code);
    }
}
