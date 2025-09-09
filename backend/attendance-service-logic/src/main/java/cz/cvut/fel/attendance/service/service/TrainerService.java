package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerReportMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainingRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.TrainingDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;

import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerService {

    private final UserRepository userRepository;
    private final TrainingUnitRepository trainingUnitRepository;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerReportRepository trainerReportRepository;

    private final TrainingUnitMapper trainingUnitMapper;
    private final TrainingMapper trainingMapper;
    private final TrainerMapper trainerMapper;
    private final TrainerReportMapper trainerReportMapper;

    private final MailService mailService;

    public List<TrainerDto> getTrainers() {
        return trainerMapper.toDtoList(trainerRepository.findAll());
    }

    public List<TrainerReportDto> getCurrentMonthReport(String email) {
        Trainer trainer = (Trainer) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));

        Month currentMonth = Month.JULY;
        int currentYear = LocalDate.now().getYear();

        DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        List<TrainerAttendance> trainingAttendances = trainer.getTrainingAttendances();

        List<TrainerReportDto> reports = trainingAttendances.stream()
                .filter(a -> a.getTrainingUnit().getDate().getMonth() == currentMonth
                        && a.getTrainingUnit().getDate().getYear() == currentYear
                        && a.isPresent() == true)
                .map(a -> {
                    String school = a.getTrainingUnit().getTraining().getSchool().getName();
                    LocalTime startTime = a.getTrainingUnit().getTraining().getStartTime();
                    LocalTime endTime = a.getTrainingUnit().getTraining().getEndTime();
                    DayOfWeek dayOfWeek = a.getTrainingUnit().getTraining().getDayOfWeek();
                    String formattedDate = a.getTrainingUnit().getDate().format(fullDateFormatter);
                    double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;

                    String type = trainer.getTrainings().contains(a.getTrainingUnit().getTraining())
                            ? "Training" : "Substitution";

                    return new TrainerReportDto(
                            formattedDate,
                            school,
                            dayOfWeek,
                            startTime,
                            endTime,
                            hours,
                            type,
                            trainer.getHourlyRate()
                    );
                })
                .collect(Collectors.toList());

        return reports;
    }

    public List<TrainingUnitDto> getUpcomingTrainingUnits(String email) {
        List<TrainingUnit> upcomingUnits = trainingUnitRepository.findUpcomingUnitsByTrainerEmail(email);
        return upcomingUnits.stream()
                .map(trainingUnitMapper::toDto)
                .toList();
    }

    public List<TrainingUnitDto> getPastTrainingUnits(String email) {
        List<TrainingUnit> pastUnits = trainingUnitRepository.findPastUnitsByTrainerEmail(email);
        return pastUnits.stream()
                .map(trainingUnitMapper::toDto)
                .toList();
    }


}
