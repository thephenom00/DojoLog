package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.ChildAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.ChildTrainingAttendance;
import cz.cvut.fel.attendance.service.model.Holiday;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerSubstitution;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.repository.ChildAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.TrainingUnitException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.ChildAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainingUnitService {

    private final TrainingUnitRepository trainingUnitRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerAttendanceRepository trainerAttendanceRepository;
    private final ChildAttendanceRepository childAttendanceRepository;

    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    private final TrainingUnitMapper trainingUnitMapper;
    private final ChildAttendanceMapper childAttendanceMapper;
    private final TrainerAttendanceMapper trainerAttendanceMapper;
    private final TrainerRepository trainerRepository;

    private final MailService mailService;

    public TrainingUnitDto getTrainingUnit(Long id) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        return trainingUnitMapper.toDto(trainingUnit);
    }

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

        List<ChildTrainingAttendance> childTrainingAttendances = trainingUnit.getChildTrainingAttendances();

        return childAttendanceMapper.toDtoList(childTrainingAttendances);
    }

    public List<TrainerAttendanceDto> getTrainerAttendances(Long id) {
        TrainingUnit trainingUnit = trainingUnitRepository.findById(id)
                .orElseThrow(() -> new TrainingUnitException("Training Unit with ID " + id + " not found", HttpStatus.NOT_FOUND));

        List<TrainerAttendance> trainerAttendances = trainingUnit.getTrainerAttendances();

        return trainerAttendanceMapper.toDtoList(trainerAttendances);
    }


//    @Scheduled(cron = "0 20 4 * * SUN") // s,m,h,dayOfMonth,month,dayOfWeek
    public void createWeeklyTrainingUnits() {
        List<Training> trainings = trainingRepository.findAll();
        List<Holiday> overlappingHolidays = new ArrayList<>();

        List<TrainingUnit> activeTrainingUnits = trainingUnitRepository.findByCurrentIsTrue();
        activeTrainingUnits.forEach(unit -> unit.setCurrent(false));
        trainingUnitRepository.saveAll(activeTrainingUnits);

        for (Training training : trainings) {
            training.getSchool().getHolidays().removeIf(holiday -> {
                LocalDate endDate = holiday.getEndDate() != null ? holiday.getEndDate() : holiday.getStartDate();
                return endDate.isBefore(LocalDate.now());
            });

            List<LocalDate> upcomingDates = training.getUpcomingDates();
            LocalDate trainingDate = upcomingDates.remove(0);
            upcomingDates.add((upcomingDates.get(upcomingDates.size() - 1)).plusDays(7));

            while (true) {
                boolean isHoliday = false;
                for (Holiday holiday : training.getSchool().getHolidays()) {
                    LocalDate startDate = holiday.getStartDate();
                    LocalDate endDate = holiday.getEndDate() != null ? holiday.getEndDate() : startDate;

                    if (!trainingDate.isBefore(startDate) && !trainingDate.isAfter(endDate)) {
                        isHoliday = true;
                        if (!overlappingHolidays.contains(holiday)) {
                            overlappingHolidays.add(holiday);
                        }
                        break;
                    }
                }

                if (isHoliday) {
                    trainingDate = upcomingDates.remove(0);
                    upcomingDates.add(upcomingDates.get(upcomingDates.size() - 1).plusDays(7));
                } else {
                    break;
                }
            }

            List<Trainer> trainers = training.getTrainers();
            List<Child> children = training.getChildren();

            LocalDate finalTrainingDate = trainingDate;

            TrainingUnit trainingUnit = new TrainingUnit();
            trainingUnit.setDate(trainingDate);
            trainingUnit.setCurrent(true);
            trainingUnit.setTraining(training);
            training.addTrainingUnit(trainingUnit);

            TrainerSubstitution trainerSubstitution = training.getTrainerSubstitutions().stream()
                    .filter(sub -> sub.getDate().equals(finalTrainingDate))
                    .findFirst()
                    .orElse(null);

            for (Trainer trainer : trainers) {
                if (trainerSubstitution != null && trainer.getId() == trainerSubstitution.getOriginalTrainerId()) {
                    trainer = trainerRepository.findById(trainerSubstitution.getSubstituteTrainerId())
                            .orElseThrow(() -> new UserException("Trainer with ID: " + " " + trainerSubstitution.getSubstituteTrainerId() + " is not existing.",
                                    HttpStatus.NOT_FOUND));
                }

                TrainerAttendance trainerAttendance = new TrainerAttendance();
                trainerAttendance.setTrainer(trainer);
                trainer.addTrainerAttendance(trainerAttendance);

                trainerAttendance.setTrainingUnit(trainingUnit);
                trainingUnit.addTrainerAttendance(trainerAttendance);

                trainerAttendance.setPresent(false);

                trainerAttendanceRepository.save(trainerAttendance);
                userRepository.save(trainer);
            }

            for (Child child : children) {
                ChildTrainingAttendance childTrainingAttendance = new ChildTrainingAttendance();
                childTrainingAttendance.setChild(child);
                child.addChildAttendance(childTrainingAttendance);

                childTrainingAttendance.setTrainingUnit(trainingUnit);
                trainingUnit.addChildAttendance(childTrainingAttendance);

                childTrainingAttendance.setPresent(false);

                childAttendanceRepository.save(childTrainingAttendance);
                childRepository.save(child);
            }

            trainingUnitRepository.save(trainingUnit);
            trainingRepository.save(training);

            sendEmail(training, overlappingHolidays, trainingDate);
        }
    }

    public void sendEmail(Training training, List<Holiday> overlappingHolidays, LocalDate trainingDate) {
        if (overlappingHolidays.isEmpty()) {
            return;
        }

        StringBuilder holidayMessage = new StringBuilder();

        for (int i = 0; i < overlappingHolidays.size(); i++) {
            Holiday holiday = overlappingHolidays.get(i);

            holidayMessage.append(holiday.getName()).append(" (");

            String startDate = holiday.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM."));
            String endDate = holiday.getEndDate() != null ? holiday.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.")) : startDate;

            if (startDate.equals(endDate)) {
                holidayMessage.append(startDate);
            } else {
                holidayMessage.append(startDate).append(" - ").append(endDate);
            }

            holidayMessage.append(")");

            if (i < overlappingHolidays.size() - 1) {
                holidayMessage.append(", ");
            }
        }

        List<String> parentEmails = training.getChildren().stream()
                .map(child -> child.getParent().getEmail())
                .collect(Collectors.toList());

        String subject = "Změna termínu tréninku z důvodu prázdnin";
        String text = "Vážení rodiče,\n\nz důvodu " + holidayMessage + " byl nejbližší trénink přesunut na nový termín: " +
                trainingDate.format(DateTimeFormatter.ofPattern("dd.MM.")) +
                "\n\nDěkujeme za pochopení.\nTým Judo SG Plzeň";

        mailService.sendMail(parentEmails, subject, text);
    }

}
