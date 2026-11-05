package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.mappers.ChildEventAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.EventMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.model.*;
import cz.cvut.fel.attendance.service.repository.*;
import cz.fel.cvut.attendance.service.exception.*;
import cz.fel.cvut.attendance.service.model.EventDto;
import cz.fel.cvut.attendance.service.model.PagedEventDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.RegisteredChildInEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final ChildRepository childRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerReportRepository trainerReportRepository;
    private final ChildEventAttendanceRepository childEventAttendanceRepository;
    private final TrainerMonthlyReportRepository trainerMonthlyReportRepository;

    private final ChildEventAttendanceMapper childEventAttendanceMapper;
    private final TrainerMapper trainerMapper;
    private final EventMapper eventMapper;

    private final MailService mailService;

    @Transactional
    public RegisteredChildInEventDto registerChild(Long id, Long childId, String description) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ChildException("Child with ID " + childId + " not found.", HttpStatus.NOT_FOUND));

        ChildEventAttendance existingAttendance = childEventAttendanceRepository.findByChildIdAndEventId(childId, id);
        if (existingAttendance != null) {
            return childEventAttendanceMapper.toDto(existingAttendance);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && event.getDeadline() != null) {
            if (LocalDate.now().isAfter(event.getDeadline())) {
                throw new EventException("Registering to this event is no longer possible.", HttpStatus.BAD_REQUEST);
            }
        }

        if (!isAdmin && event.getPlaces() > 0 && event.getChildEventAttendances().size() >= event.getPlaces()) {
            throw new EventException("Event has full capacity.", HttpStatus.BAD_REQUEST);
        }

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

    @Transactional
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

        LocalDate attributionDate = event.getEndDate() != null ? event.getEndDate() : event.getStartDate();
        TrainerMonthlyReport monthlyReport = getOrCreateMonthlyReport(
                trainer, attributionDate.getYear(), attributionDate.getMonthValue()
        );

        TrainerReport trainerReport = new TrainerReport(
            event.getStartDate(),
            event.getEndDate(),
            event.getName(),
            (event.getEndDate() == null || event.getStartDate().equals(event.getEndDate())) ? event.getStartDate().getDayOfWeek() : null,
            event.getEventType(),
            event.getId(),
            event.getStartTime(),
            event.getEndTime(),
            event.getTrainerSalary()
        );

        trainerReport.setTrainerMonthlyReport(monthlyReport);

        trainerReportRepository.save(trainerReport);

        sendInfoMailForTrainer(event, trainer.getEmail(), true);

        return trainerMapper.toDto(trainer);
    }

    @Transactional
    public void removeChild(Long id, Long childId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));

        ChildEventAttendance attendance = childEventAttendanceRepository.findByChildIdAndEventId(childId, id);
        if (attendance == null) {
            attendance = childEventAttendanceRepository.findById(childId).orElse(null);
        }
        if (attendance == null) {
            throw new AttendanceException("Child is not registered at this event.", HttpStatus.NOT_FOUND);
        }

        event.getChildEventAttendances().remove(attendance);
        childEventAttendanceRepository.delete(attendance);
    }

    @Transactional
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

        trainerReportRepository.deleteTrainerReport(trainerId, event.getId(), event.getEventType());

        sendInfoMailForTrainer(event, trainer.getEmail(), false);
    }


    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        if (eventRepository.existsByName(eventDto.name())) {
            throw new SchoolException("Event with name '" + eventDto.name() + "' already exists.",
                    HttpStatus.CONFLICT);
        }

        Event event = eventMapper.toEntity(eventDto);
        eventRepository.save(event);

        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " not found.", HttpStatus.NOT_FOUND));
        return eventMapper.toDto(event);
    }

    public PagedEventDto getUpcomingEvents(int page, int size) {
        Page<Event> eventsPage = eventRepository.findUpcomingEvents(PageRequest.of(page, size));
        List<EventDto> dtos = eventMapper.toDtoList(eventsPage.getContent());

        return new PagedEventDto(dtos, eventsPage.getNumber(), eventsPage.getTotalPages(), eventsPage.getTotalElements());
    }

    public PagedEventDto getPastEvents(int page, int size) {
        Page<Event> eventsPage = eventRepository.findPastEvents(PageRequest.of(page, size));
        List<EventDto> dtos = eventMapper.toDtoList(eventsPage.getContent());

        return new PagedEventDto(dtos, eventsPage.getNumber(), eventsPage.getTotalPages(), eventsPage.getTotalElements());
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        for (Trainer trainer : new ArrayList<>(event.getTrainers())) {
            trainer.getEvents().remove(event);
            trainerRepository.save(trainer);
        }
        event.getTrainers().clear();

        trainerReportRepository.deleteAllBySourceIdAndReportCategory(event.getId(), event.getEventType());

        eventRepository.delete(event);
    }

    @Transactional
    public EventDto updateEvent(Long id, EventDto eventDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        ReportCategory oldCategory = event.getEventType();

        eventMapper.updateEventFromDto(eventDto, event);
        eventRepository.save(event);

        List<TrainerReport> existingReports = trainerReportRepository.findAllBySourceIdAndReportCategory(event.getId(), oldCategory);
        LocalDate newAttributionDate = event.getEndDate() != null ? event.getEndDate() : event.getStartDate();
        int newYear = newAttributionDate.getYear();
        int newMonth = newAttributionDate.getMonthValue();

        for (TrainerReport report : existingReports) {
            TrainerMonthlyReport currentMonthlyReport = report.getTrainerMonthlyReport();
            if (currentMonthlyReport.getStatus() == MonthlyReportStatus.PAID) {
                continue;
            }

            report.setStartDate(event.getStartDate());
            report.setEndDate(event.getEndDate());
            report.setName(event.getName());
            report.setDayOfWeek((event.getEndDate() == null || event.getStartDate().equals(event.getEndDate()))
                    ? event.getStartDate().getDayOfWeek() : null);
            report.setReportCategory(event.getEventType());
            report.setStartTime(event.getStartTime());
            report.setEndTime(event.getEndTime());
            report.setMoney(event.getTrainerSalary());

            if (currentMonthlyReport.getYear() != newYear || currentMonthlyReport.getMonth() != newMonth) {
                Trainer trainer = currentMonthlyReport.getTrainer();
                TrainerMonthlyReport newMonthlyReport = getOrCreateMonthlyReport(trainer, newYear, newMonth);
                report.setTrainerMonthlyReport(newMonthlyReport);
            }
        }
        trainerReportRepository.saveAll(existingReports);

        return eventMapper.toDto(event);
    }

    public List<RegisteredChildInEventDto> getRegisteredChildren(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String email = auth != null ? auth.getName() : null;
            boolean isAssignedTrainer = event.getTrainers().stream()
                    .anyMatch(t -> t.getEmail() != null && t.getEmail().equalsIgnoreCase(email));
            if (!isAssignedTrainer) {
                throw new EventException("Access denied: only assigned trainers or admin can view registered children.", HttpStatus.FORBIDDEN);
            }
        }

        return childEventAttendanceMapper.toDtoList(event.getChildEventAttendances());
    }

    public List<TrainerDto> getRegisteredTrainers(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventException("Event with ID: " + id + " is not existing.", HttpStatus.NOT_FOUND));

        return trainerMapper.toDtoList(event.getTrainers());
    }

    public void sendInfoMailForTrainer(Event event, String email, boolean add) {
        mailService.sendTrainerEventInfoMail(email, event, add);
    }

    public void sendRegistrationMailForParents(Child child, Event event) {
        mailService.sendChildEventRegistrationMail(child, event);
    }

    private TrainerMonthlyReport getOrCreateMonthlyReport(Trainer trainer, int year, int month) {
        return trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(trainer.getId(), year, month)
                .orElseGet(() -> trainerMonthlyReportRepository.save(
                        new TrainerMonthlyReport(trainer, year, month)
                ));
    }
}
