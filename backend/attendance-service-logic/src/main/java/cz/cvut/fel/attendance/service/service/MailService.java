package cz.cvut.fel.attendance.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendMail(List<String> recipients, String subject, String text) {
        for (String recipient : recipients) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("judosgplzen@gmail.com");
            message.setTo(recipient);
            message.setText(text);
            message.setSubject(subject);

            mailSender.send(message);
            System.out.println("Email sent to " + recipient + ".");
        }
    }

}
