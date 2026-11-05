package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.*;
import cz.cvut.fel.attendance.service.repository.*;
import cz.fel.cvut.attendance.service.exception.TrainingUnitException;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainingUnitService {

    @Autowired
    @Lazy
    private TrainingUnitService self;

    private final TrainingUnitRepository trainingUnitRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerAttendanceRepository trainerAttendanceRepository;
    private final ChildAttendanceRepository childAttendanceRepository;
    private final HolidayRepository holidayRepository;
    private final AppSettingsRepository appSettingsRepository;

    private final TrainingUnitMapper trainingUnitMapper;
    private final ChildAttendanceMapper childAttendanceMapper;
    private final TrainerAttendanceMapper trainerAttendanceMapper;

    private final MailService mailService;
    private final TrainingScheduleRepository trainingScheduleRepository;

    public TrainingUnitDto getTrainingUnit(Long id) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainingUnitMapper.toDto(trainingUnit);
    }

    @Transactional
    public TrainingUnitDto updateDescription(Long id, String description) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        trainingUnit.setDescription(description);
        trainingUnitRepository.save(trainingUnit);

        return trainingUnitMapper.toDto(trainingUnit);
    }

    public List<ChildAttendanceDto> getChildAttendances(Long id) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return childAttendanceMapper.toDtoList(trainingUnit.getChildTrainingAttendances());
    }

    public List<TrainerAttendanceDto> getTrainerAttendances(Long id) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainerAttendanceMapper.toDtoList(trainingUnit.getTrainerAttendances());
    }

    @Transactional
    public void createAttendanceForCurrentUnits(Trainer trainer, Training training) {
        for (TrainingSchedule schedule : training.getTrainingSchedules()) {
            for (TrainingUnit unit : schedule.getTrainingUnits()) {
                if (!unit.isCurrent()) {
                    continue;
                }
                if (trainerAttendanceRepository.existsByTrainer_IdAndTrainingUnit_Id(trainer.getId(), unit.getId())) {
                    continue;
                }

                TrainerAttendance trainerAttendance = new TrainerAttendance();
                trainerAttendance.setTrainer(trainer);
                trainerAttendance.setTrainingUnit(unit);
                trainerAttendance.setPresent(false);
                trainer.addTrainerAttendance(trainerAttendance);
                unit.addTrainerAttendance(trainerAttendance);
                trainerAttendanceRepository.save(trainerAttendance);
            }
        }
    }

    @Transactional
    @Scheduled(cron = "0 20 4 * * SUN", zone = "Europe/Prague") // s,m,h,dayOfMonth,month,dayOfWeek
    public void createWeeklyTrainingUnits() {
        AppSettings appSettings = appSettingsRepository.findById(1L).orElseThrow();
        if (!appSettings.isTrainingGeneration()) { return; }

        LocalDate today = LocalDate.now(ZoneId.of("Europe/Prague"));
        holidayRepository.deleteOutdatedHolidays(today);

        for (Training training : trainingRepository.findAll()) {
            if (!training.isActive()) continue;

            try {
                self.createWeeklyTrainingUnitsForTraining(training, today);
            } catch (Exception e) {
                log.error("Failed to create weekly training units for training ID {} (cause type: {}).",
                        training.getId(), e.getClass().getSimpleName());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createWeeklyTrainingUnitsForTraining(Training training, LocalDate today) {
        List<Child> children = training.getChildren();
        List<Trainer> trainers = training.getTrainers();
        List<Holiday> holidays = training.getSchool().getHolidays();

        for (TrainingSchedule schedule : training.getTrainingSchedules()) {
            TrainingUnit currentUnit = trainingScheduleRepository.findCurrentTrainingUnitById(schedule.getId());
            LocalDate potentialNextDate = today.with(TemporalAdjusters.nextOrSame(schedule.getDayOfWeek()));
            Holiday holiday = findHoliday(potentialNextDate, holidays);
            LocalDate nextDate = (holiday != null)
                    ? findFirstTrainingAfterHolidays(potentialNextDate, holidays)
                    : potentialNextDate;

            final LocalDate dateToCheck = nextDate;
            boolean alreadyExists = schedule.getTrainingUnits().stream()
                    .anyMatch(unit -> dateToCheck.equals(unit.getDate()));
            if (alreadyExists) { continue; }


            // Send one notice when a continuous holiday period begins.
            if (holiday != null && findHoliday(potentialNextDate.minusWeeks(1), holidays) == null) {
                sendMailsAboutHoliday(training, schedule.getDayOfWeek(), holiday, nextDate);
            }

            // Mark previous current unit as past
            if (currentUnit != null) {
                currentUnit.setCurrent(false);
                trainingUnitRepository.save(currentUnit);
            }

            // Create new training unit for next week
            TrainingUnit trainingUnit = new TrainingUnit();
            trainingUnit.setTrainingSchedule(schedule);
            trainingUnit.setCurrent(true);
            trainingUnit.setDate(nextDate);
            trainingUnitRepository.save(trainingUnit);

            // False training attendance for every trainer in training
            for (Trainer trainer : trainers) {
                TrainerAttendance trainerAttendance = new TrainerAttendance();
                trainerAttendance.setTrainer(trainer);
                trainerAttendance.setTrainingUnit(trainingUnit);
                trainerAttendance.setPresent(false);
                trainerAttendanceRepository.save(trainerAttendance);
            }

            // False training attendance for every child in training
            for (Child child : children) {
                ChildTrainingAttendance childTrainingAttendance = new ChildTrainingAttendance();
                childTrainingAttendance.setChild(child);
                childTrainingAttendance.setTrainingUnit(trainingUnit);
                childTrainingAttendance.setPresent(false);
                childAttendanceRepository.save(childTrainingAttendance);
            }

            schedule.getTrainingUnits().add(trainingUnit);
            trainingScheduleRepository.save(schedule);
        }
    }

    private Holiday findHoliday(LocalDate date, List<Holiday> holidays) {
        for (Holiday h : holidays) {
            LocalDate start = h.getStartDate();
            LocalDate end = h.getEndDate() != null ? h.getEndDate() : start;
            if (!date.isBefore(start) && !date.isAfter(end)) {
                return h;
            }
        }
        return null;
    }

    private LocalDate findFirstTrainingAfterHolidays(LocalDate startDate, List<Holiday> holidays) {
        LocalDate candidate = startDate.plusWeeks(1);
        int maxWeeks = 52; // max 1 year

        while (maxWeeks-- > 0) {
            if (findHoliday(candidate, holidays) == null) {
                return candidate;
            }
            candidate = candidate.plusWeeks(1);
        }
        return candidate;
    }

    private void sendMailsAboutHoliday(Training training, DayOfWeek dayOfWeek, Holiday holiday, LocalDate nextTrainingDate) {
        Set<String> recipientEmails = new HashSet<>();
        for (Child child : training.getChildren()) {
            if (child.getParent() != null && child.getParent().getEmail() != null) {
                recipientEmails.add(child.getParent().getEmail());
            }
        }
        for (Trainer trainer : training.getTrainers()) {
            if (trainer.getEmail() != null) {
                recipientEmails.add(trainer.getEmail());
            }
        }
        mailService.sendHolidayRescheduleMail(
                training.getSchool().getName(),
                dayOfWeek,
                holiday,
                nextTrainingDate,
                new ArrayList<>(recipientEmails)
        );
    }
}
