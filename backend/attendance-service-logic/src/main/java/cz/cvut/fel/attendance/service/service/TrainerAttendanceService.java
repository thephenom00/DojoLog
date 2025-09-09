package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.mappers.TrainerAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerReportMapper;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.fel.cvut.attendance.service.exception.AttendanceException;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerAttendanceService {

    private final TrainerAttendanceRepository trainerAttendanceRepository;
    private final TrainerAttendanceMapper trainerAttendanceMapper;

    private final TrainerReportRepository trainerReportRepository;

    public TrainerAttendanceDto markPresent(Long id) {
        TrainerAttendance trainerAttendance = trainerAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        trainerAttendance.setPresent(true);
        trainerAttendanceRepository.save(trainerAttendance);

//        Trainer trainer = trainerAttendance.getTrainer();
//        String school = trainerAttendance.getTrainingUnit().getTraining().getSchool().getName();
//        LocalTime startTime = trainerAttendance.getTrainingUnit().getTraining().getStartTime();
//        LocalTime endTime = trainerAttendance.getTrainingUnit().getTraining().getEndTime();
//        DayOfWeek dayOfWeek = trainerAttendance.getTrainingUnit().getTraining().getDayOfWeek();
//        String formattedDate = trainerAttendance.getTrainingUnit().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//
//        double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;
//
//        String type = trainer.getTrainings().contains(trainerAttendance.getTrainingUnit().getTraining()) ? "Training" : "Substitution";

//        TrainerReport report = new TrainerReport(
//                trainer.getId(),
//                trainerAttendance.getId(),
//                formattedDate,
//                school,
//                dayOfWeek,
//                startTime,
//                endTime,
//                hours,
//                type,
//                trainer.getHourlyRate()
//        );
//
//        trainerReportRepository.save(report);
        return trainerAttendanceMapper.toDto(trainerAttendance);
    }

    public TrainerAttendanceDto markAbsent(Long id) {
        TrainerAttendance trainerAttendance = trainerAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        trainerAttendance.setPresent(false);

        trainerAttendanceRepository.save(trainerAttendance);

        return trainerAttendanceMapper.toDto(trainerAttendance);
    }
}
