package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.model.Parent;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import cz.fel.cvut.attendance.service.model.auth.TrainerRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.getDayName;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService  {

    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;

    @Value("${admin.email}")
    private String adminEmail;

    public HttpStatus changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return HttpStatus.UNAUTHORIZED;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return HttpStatus.OK;
    }

    public void registerParent(ParentRegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserException("User with email: " + request.email() + " already exists.", HttpStatus.CONFLICT);
        }

        Parent parent = new Parent(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                passwordEncoder.encode(request.password())
        );

        try {
            userRepository.save(parent);
            sendConfirmationMail(request);
            sendParentRegistrationMail(parent);
        } catch (Exception e) {
            throw new UserException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void registerTrainer(TrainerRegisterRequest request, String password) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserException("User with email: " + request.email() + " already exists.", HttpStatus.CONFLICT);
        } else if (userRepository.findByPhoneNumber(request.phoneNumber()).isPresent()) {
            throw new UserException("User with phone number: " + request.phoneNumber() + " already exists.", HttpStatus.CONFLICT);
        }

        Trainer trainer = new Trainer(
                request.firstName(),
                request.lastName(),
                request.email(),
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
            }
            sendTrainerRegistrationMail(password, trainer, training);
        } catch (Exception e) {
            throw new UserException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User with email: " + email + " not found.", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        throw new UserException("Invalid credentials. Please check your email and password.", HttpStatus.UNAUTHORIZED);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        List<GrantedAuthority> authorities = List.of(() -> user.getRole().toString());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public void sendTrainerRegistrationMail(String password, Trainer trainer, Training training) {
        StringBuilder text = new StringBuilder();
        text.append("Vážený trenére,\n\n")
                .append("dovolujeme si Vám oznámit, že jste byl úspěšně zaregistrován do platformy DojoLog.\n\n")
                .append("Vaše přihlašovací údaje:\n")
                .append("- Email: ").append(trainer.getEmail()).append("\n")
                .append("- Heslo: ").append(password).append("\n\n")
                .append("Heslo si samozřejmě můžete kdykoliv změnit po přihlášení do platformy.\n\n");

        if (training != null) {
            text.append("Dále jsme Vás přiřadili k následujícímu tréninku:\n\n")
                    .append("- Škola: ").append(training.getSchool().getName()).append("\n")
                    .append("- Skupina: ").append(training.getName()).append("\n")
                    .append("- Den v týdnu: ").append(getDayName(training.getDayOfWeek())).append("\n")
                    .append("- Čas: ").append(training.getStartTime()).append(" - ").append(training.getEndTime()).append("\n\n");
        }

        text.append("Podrobnosti naleznete na: https://dojolog.vercel.app\n\n")
                .append("V případě jakýchkoli dotazů se prosím obraťte na administrátora.\n\n")
                .append("Děkujeme za spolupráci.\n\n")
                .append("S pozdravem,\nTým Judo SG Plzeň");

        String subject = "Byl jste zaregistrován do platformy DojoLog";
        mailService.sendMail(List.of(trainer.getEmail()), subject, text.toString());
    }


    private void sendConfirmationMail(ParentRegisterRequest request) {
        String text = "Byl zaregistrován nový rodič s následujícími údaji:\n\n" +
                "Jméno: " + request.firstName() + "\n" +
                "Příjmení: " + request.lastName() + "\n" +
                "E-mail: " + request.email() + "\n" +
                "Telefon: " + request.phoneNumber() + "\n\n";

        mailService.sendMail(List.of(adminEmail), "Nový rodič zaregistrován", text);
    }

    private void sendParentRegistrationMail(Parent parent) {
        String subject = "Registrace do systému DojoLog";
        String text = "Vážený rodiči,\n\n" +
                "Děkujeme, že jste se zaregistrovali do platformy DojoLog.\n\n" +
                "Nyní se můžete přihlásit do systému a registrovat své dítě na vybrané tréninky či akce.\n\n" +
                "Přístup k platformě: https://dojolog.vercel.app\n\n" +
                "V případě dotazů nás neváhejte kontaktovat.\n\n" +
                "S pozdravem,\nTým Judo SG Plzeň";

        mailService.sendMail(List.of(parent.getEmail()), subject, text);
    }



}
