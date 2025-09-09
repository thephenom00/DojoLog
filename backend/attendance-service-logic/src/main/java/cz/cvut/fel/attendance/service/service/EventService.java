package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.Role;
import cz.cvut.fel.attendance.service.mappers.ChildEventAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.EventMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.ChildEventAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.EventRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.ChildException;
import cz.fel.cvut.attendance.service.exception.EventException;
import cz.fel.cvut.attendance.service.exception.SchoolException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.EventDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ChildRepository childRepository;
    private final TrainerRepository trainerRepository;
    private final ChildEventAttendanceRepository childEventAttendanceRepository;
    private final TrainerReportRepository trainerReportRepository;

    private final ChildEventAttendanceMapper childEventAttendanceMapper;
    private final TrainerMapper trainerMapper;
    private final EventMapper eventMapper;

    private final MailService mailService;

    public RegisteredChildInEventDto registerChild(Long id, Long childId, String description) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ChildException("Child with ID " + childId + " not found.", HttpStatus.NOT_FOUND));

        ChildEventAttendance childEventAttendance = new ChildEventAttendance();
        childEventAttendance.setEvent(event);
        childEventAttendance.setChild(child);
        childEventAttendance.setPresent(false);
        childEventAttendance.setPaymentReceived(false);

        if (description != null && !description.isBlank()) {
            childEventAttendance.setNote(description);
        }

        childEventAttendance = childEventAttendanceRepository.save(childEventAttendance);

        sendRegistrationMailForParents(child, event);
        return childEventAttendanceMapper.toDto(childEventAttendance);
    }

    public TrainerDto addTrainer(Long id, Long trainerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserException("Trainer with ID: " + id + " was not found.",
                        HttpStatus.NOT_FOUND));
        trainer.getEvents().add(event);
        trainerRepository.save(trainer);

        event.getTrainers().add(trainer);
        eventRepository.save(event);

        String date = "";
        DayOfWeek dayOfWeek = null;
        if (event.getEndDate() == null) {
            dayOfWeek = event.getStartDate().getDayOfWeek();
            date = event.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } else {
            date = event.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.")) + " - " +
                    event.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM."));
        }
        String location = event.getName();
        LocalTime startTime = event.getStartTime() == null ? null : event.getStartTime();
        LocalTime endTime =  event.getEndTime() == null ? null : event.getEndTime();

        TrainerReport report = new TrainerReport(
                trainerId,
                null,
                date,
                location,
                dayOfWeek,
                startTime,
                endTime,
                0,
                event.getEventType(),
                event.getTrainerSalary()
        );

        trainerReportRepository.save(report);

        sendInfoMailForTrainer(event, trainer.getEmail(), true);

        return trainerMapper.toDto(trainer);
    }

    public void removeTrainer(Long id, Long trainerId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserException("Trainer with ID: " + id + " was not found.",
                        HttpStatus.NOT_FOUND));
        trainer.getEvents().remove(event);
        trainerRepository.save(trainer);

        event.getTrainers().remove(trainer);
        eventRepository.save(event);

        TrainerReport trainerReport = trainerReportRepository.findByTrainerIdAndStartTimeAndEndTimeAndName(trainerId, event.getStartTime(), event.getEndTime(), event.getName());
        trainerReportRepository.delete(trainerReport);

        sendInfoMailForTrainer(event, trainer.getEmail(), false);
    }


    public EventDto createEvent(EventDto eventDto) {
        if (eventRepository.existsByName(eventDto.name())) {
            throw new SchoolException("Event with name '" + eventDto.name() + "' already exists.",
                    HttpStatus.CONFLICT);
        }

        Event event = eventMapper.toEntity(eventDto);
        eventRepository.save(event);

        sendInfoMailForParents(eventDto);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        return eventMapper.toDto(event);
    }

    public List<EventDto> getEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.toDtoList(events);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        eventRepository.delete(event);
    }

    public EventDto updateEvent(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        eventMapper.updateEventFromDto(eventDto, event);

        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<RegisteredChildInEventDto> getRegisteredChildren(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        return childEventAttendanceMapper.toDtoList(event.getChildEventAttendances());
    }

    public List<TrainerDto> getRegisteredTrainers(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        return trainerMapper.toDtoList(event.getTrainers());
    }

    public void sendInfoMailForParents(EventDto eventDto) {
        List<String> parentEmails = userRepository.findAll().stream()
                .filter(u->u.getRole().equals(Role.ROLE_PARENT))
                .map(User::getEmail)
                .toList();

        String subject = "Byla zveřejněna nová událost";
        String text = "Vážení rodiče,\n\n" +
                "byla zveřejněna nová událost: " + eventDto.name() + ".\n\n" +
                "Místo: " + eventDto.location() + "\n" +
                "Datum: " + eventDto.startDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + (eventDto.endDate() != null ? " - " + eventDto.endDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "") + "\n" +
                "Popis: " + eventDto.description() + "\n\n" +
                "Máte-li zájem zaregistrovat Vaše dítě, navštivte prosím naši webovou stránku:\n" +
                "https://dojolog.vercel.app/events\n\n" +
                "Děkujeme, že využíváte naši platformu.\n" +
                "Tým Judo SG Plzeň";

        mailService.sendMail(parentEmails, subject, text);
    }

    public void sendInfoMailForTrainer(Event event, String email, boolean add) {
        String subject = add
                ? "Byl jste přidán do události"
                : "Byl jste odebrán z události";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = event.getStartDate().format(formatter) +
                (event.getEndDate() != null ? " - " + event.getEndDate().format(formatter) : "");

        String text;
        if (add) {
            text = "Vážený trenére,\n\n" +
                    "oznamujeme Vám, že byl jste přidán do události: " + event.getName() + "\n\n" +
                    "Datum: " + date + "\n" +
                    "Místo: " + event.getLocation() + "\n" +
                    "Odměna: " + event.getTrainerSalary() + " Kč\n\n" +
                    "Děkujeme,\nTým Judo SG Plzeň";
        } else {
            text = "Vážený trenére,\n\n" +
                    "oznamujeme Vám byl jste odebrán z události: " + event.getName() + "\n\n" +
                    "Děkujeme, že využíváte naši platformu.\nTým Judo SG Plzeň";
        }

        mailService.sendMail(List.of(email), subject, text);
    }

    public void sendRegistrationMailForParents(Child child, Event event) {
        String email = child.getParent().getEmail();

        String subject = "Registrace Vašeho dítěte na událost " + event.getName();
        String text =
                "Vážený rodiči,\n\n" +
                        "Vaše dítě " + child.getFirstName() + " " + child.getLastName() +
                        " bylo úspěšně zaregistrováno na událost:\n\n" +
                        "- Název: " + event.getName() + "\n" +
                        "- Místo: " + event.getLocation() + "\n" +
                        "- Datum: " + event.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                        (event.getEndDate() != null ? " - " + event.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "") + "\n\n" +
                        "Děkujeme, že využíváte naši platformu.\n\n" +
                        "Tým Judo SG Plzeň";

        mailService.sendMail(List.of(email), subject, text);
    }





}
