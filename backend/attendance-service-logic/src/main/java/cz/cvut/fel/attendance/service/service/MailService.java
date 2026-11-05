package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.model.*;
import cz.fel.cvut.attendance.service.model.ChildDto;
import cz.fel.cvut.attendance.service.model.NewsDto;
import cz.fel.cvut.attendance.service.model.auth.ParentRegisterRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cz.cvut.fel.attendance.service.utils.HelpMethods.getDayName;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.");

    private final JavaMailSender mailSender;
    private final PdfGenerationService pdfGenerationService;
    private final Environment environment;

    @Value("${mail.from:}")
    private String mailFrom;

    public void sendMail(List<String> recipients, String subject, String text) {
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        int batchSize = 50;
        for (int start = 0; start < recipients.size(); start += batchSize) {
            int end = Math.min(start + batchSize, recipients.size());
            List<String> batch = recipients.subList(start, end);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setSubject(subject);
            message.setText(text);
            if (batch.size() == 1) {
                message.setTo(batch.get(0));
            } else {
                message.setBcc(batch.toArray(new String[0]));
            }

            try {
                mailSender.send(message);
                Thread.sleep(1000);
            } catch (Exception e) {
                log.warn("Unable to send notification email batch.");
            }
        }
    }

    public void sendMailWithAttachment(String recipient, String subject, String text,
                                       byte[] attachmentBytes, String attachmentFilename) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(text);
            helper.addAttachment(attachmentFilename, new ByteArrayResource(attachmentBytes));
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.warn("Unable to send email with a PDF attachment.");
        }
    }

    @Async
    public void sendTrainerEventInfoMail(String email, Event event, boolean added) {
        String eventDate = event.getStartDate().format(DATE_FORMAT)
                + (event.getEndDate() != null ? " – " + event.getEndDate().format(DATE_FORMAT) : "");
        String action = added ? "ADDED" : "REMOVED";
        String subject = render("TRAINER_EVENT_" + action + "_SUBJECT");
        String text = render("TRAINER_EVENT_" + action + "_BODY",
                "eventName", event.getName(),
                "eventDate", eventDate,
                "eventLocation", event.getLocation(),
                "trainerSalary", event.getTrainerSalary());
        sendMail(List.of(email), subject, text);
    }

    @Async
    public void sendChildEventRegistrationMail(Child child, Event event) {
        String eventDate = event.getStartDate().format(DATE_FORMAT)
                + (event.getEndDate() != null ? " – " + event.getEndDate().format(DATE_FORMAT) : "");
        String subject = render("CHILD_EVENT_REGISTRATION_SUBJECT", "eventName", event.getName());
        String text = render("CHILD_EVENT_REGISTRATION_BODY",
                "childName", child.getFirstName() + " " + child.getLastName(),
                "eventName", event.getName(),
                "eventLocation", event.getLocation(),
                "eventDate", eventDate);
        sendMail(List.of(child.getParent().getEmail()), subject, text);
    }

    @Async
    public void sendChildRegistrationMail(Parent parent, ChildDto childDto, String adminEmail) {
        String childName = childDto.firstName() + " " + childDto.lastName();
        String parentName = parent.getFirstName() + " " + parent.getLastName();
        String adminSubject = render("CHILD_REGISTRATION_ADMIN_SUBJECT");
        String adminText = render("CHILD_REGISTRATION_ADMIN_BODY",
                "childName", childName,
                "parentName", parentName,
                "parentEmail", parent.getEmail());
        String parentSubject = render("CHILD_REGISTRATION_PARENT_SUBJECT");
        String parentText = render("CHILD_REGISTRATION_PARENT_BODY", "childName", childName);

        sendMail(List.of(adminEmail), adminSubject, adminText);
        sendMail(List.of(parent.getEmail()), parentSubject, parentText);
    }

    @Async
    public void sendChildApprovedMail(Child child, Training training) {
        String dayTime = training.getTrainingSchedules().stream()
                .map(schedule -> getDayName(schedule.getDayOfWeek()) + " "
                        + schedule.getStartTime() + " - " + schedule.getEndTime())
                .collect(Collectors.joining(", "));
        String subject = render("CHILD_APPROVED_SUBJECT");
        String text = render("CHILD_APPROVED_BODY",
                "childName", child.getFirstName() + " " + child.getLastName(),
                "schoolName", training.getSchool().getName(),
                "trainingName", training.getName(),
                "dayTime", dayTime,
                "amount", training.getPrice());
        sendMail(List.of(child.getParent().getEmail()), subject, text);
    }

    @Async
    public void sendTrainerRegistrationMail(String password, Trainer trainer, Training training) {
        String trainingBlock = "";
        if (training != null) {
            String scheduleRows = training.getTrainingSchedules().stream()
                    .map(schedule -> render("TRAINER_REGISTRATION_SCHEDULE_ROW",
                            "dayName", Objects.toString(getDayName(schedule.getDayOfWeek()), ""),
                            "startTime", schedule.getStartTime(),
                            "endTime", schedule.getEndTime()))
                    .collect(Collectors.joining("\n"));
            trainingBlock = render("TRAINER_REGISTRATION_TRAINING_BLOCK",
                    "schoolName", training.getSchool().getName(),
                    "trainingName", training.getName(),
                    "scheduleRows", scheduleRows);
        }

        String subject = render("TRAINER_REGISTRATION_SUBJECT");
        String text = render("TRAINER_REGISTRATION_BODY",
                "email", trainer.getEmail(),
                "password", password,
                "trainingBlock", trainingBlock);
        sendMail(List.of(trainer.getEmail()), subject, text);
    }

    @Async
    public void sendNewParentNotificationMail(ParentRegisterRequest request, String adminEmail) {
        String subject = render("NEW_PARENT_NOTIFICATION_SUBJECT");
        String text = render("NEW_PARENT_NOTIFICATION_BODY",
                "firstName", request.firstName(),
                "lastName", request.lastName(),
                "email", request.email(),
                "phoneNumber", request.phoneNumber());
        sendMail(List.of(adminEmail), subject, text);
    }

    @Async
    public void sendParentRegistrationMail(Parent parent) {
        String subject = render("PARENT_REGISTRATION_SUBJECT");
        String text = render("PARENT_REGISTRATION_BODY",
                "parentName", parent.getFirstName() + " " + parent.getLastName());
        sendMail(List.of(parent.getEmail()), subject, text);
    }

    @Async
    public void sendPasswordResetMail(String email, String code) {
        sendMail(List.of(email),
                render("PASSWORD_RESET_SUBJECT"),
                render("PASSWORD_RESET_BODY", "code", code));
    }

    @Async
    public void sendRegistrationOtpMail(String email, String code) {
        sendMail(List.of(email),
                render("REGISTRATION_OTP_SUBJECT"),
                render("REGISTRATION_OTP_BODY", "code", code));
    }

    @Async
    public void sendTrainerSubstitutionMail(Trainer originalTrainer, Trainer substituteTrainer,
                                            TrainingSchedule trainingSchedule, LocalDate date) {
        List<String> recipients = new ArrayList<>(trainingSchedule.getTraining().getChildren().stream()
                .map(child -> child.getParent().getEmail())
                .toList());
        recipients.add(originalTrainer.getEmail());
        recipients.add(substituteTrainer.getEmail());

        String subject = render("TRAINER_SUBSTITUTION_SUBJECT", "date", date.format(DATE_FORMAT));
        String text = render("TRAINER_SUBSTITUTION_BODY",
                "trainingName", trainingSchedule.getTraining().getName(),
                "schoolName", trainingSchedule.getTraining().getSchool().getName(),
                "dayName", Objects.toString(getDayName(trainingSchedule.getDayOfWeek()), ""),
                "date", date.format(DATE_FORMAT),
                "startTime", trainingSchedule.getStartTime(),
                "endTime", trainingSchedule.getEndTime(),
                "originalTrainerName", originalTrainer.getFirstName() + " " + originalTrainer.getLastName(),
                "substituteTrainerName", substituteTrainer.getFirstName() + " " + substituteTrainer.getLastName(),
                "substituteTrainerPhone", substituteTrainer.getPhoneNumber());
        sendMail(recipients, subject, text);
    }

    @Async
    public void sendHolidayRescheduleMail(String schoolName,
                                          DayOfWeek dayOfWeek,
                                          Holiday holiday,
                                          LocalDate nextTrainingDate,
                                          List<String> recipientEmails) {
        if (holiday == null || recipientEmails.isEmpty()) {
            return;
        }

        LocalDate startDate = holiday.getStartDate();
        LocalDate endDate = holiday.getEndDate() != null ? holiday.getEndDate() : startDate;
        boolean multiDay = !startDate.equals(endDate);
        String start = startDate.format(multiDay ? SHORT_DATE_FORMAT : DATE_FORMAT);
        String end = endDate.format(DATE_FORMAT);
        String nextTraining = nextTrainingDate == null
                ? ""
                : render("HOLIDAY_NEXT_TRAINING",
                        "dayName", Objects.toString(getDayName(dayOfWeek), ""),
                        "date", nextTrainingDate.format(DATE_FORMAT));
        String templateType = multiDay ? "MULTI_DAY" : "SINGLE_DAY";

        String subject = render("HOLIDAY_" + templateType + "_SUBJECT",
                "startDate", start,
                "endDate", end,
                "schoolName", schoolName);
        String text = render("HOLIDAY_" + templateType + "_BODY",
                "startDate", start,
                "endDate", end,
                "schoolName", schoolName,
                "holidayName", holiday.getName(),
                "nextTraining", nextTraining);
        sendMail(recipientEmails, subject, text);
    }

    @Async
    public void sendNewsMail(List<String> allUserEmails, NewsDto newsDto) {
        String subject = render("NEWS_SUBJECT");
        String text = render("NEWS_BODY", "newsDescription", newsDto.description());
        sendMail(allUserEmails, subject, text);
    }

    @Async
    public void sendCustomTrainingMail(List<String> parentEmails, String subject, String message) {
        sendMail(parentEmails, subject, message);
    }

    private String render(String templateName, Object... fields) {
        String propertyName = "MAIL_TEMPLATE_" + templateName;
        String template = environment.getProperty(propertyName);
        if (template == null || template.isBlank()) {
            throw new IllegalStateException("Missing email template configuration: " + propertyName);
        }

        String rendered = template.replace("\\n", "\n");
        for (int i = 0; i < fields.length; i += 2) {
            String placeholder = "{{" + fields[i] + "}}";
            rendered = rendered.replace(placeholder, Objects.toString(fields[i + 1], ""));
        }
        if (rendered.contains("{{")) {
            throw new IllegalStateException("Unresolved placeholder in email template: " + propertyName);
        }
        return rendered;
    }
}
