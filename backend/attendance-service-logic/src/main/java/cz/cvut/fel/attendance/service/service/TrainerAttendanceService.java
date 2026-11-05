package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.mappers.TrainerAttendanceMapper;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerMonthlyReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.fel.cvut.attendance.service.exception.AttendanceException;
import cz.fel.cvut.attendance.service.model.TrainerAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerAttendanceService {
    private final TrainerMonthlyReportRepository trainerMonthlyReportRepository;
    private final TrainerAttendanceRepository trainerAttendanceRepository;
    private final TrainerReportRepository trainerReportRepository;
    private final TrainerAttendanceMapper trainerAttendanceMapper;

    @Transactional
    public TrainerAttendanceDto markPresent(Long id) {
        TrainerAttendance trainerAttendance = trainerAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        if (trainerAttendance.isPresent()) {
            return null;
        }

        LocalDate unitDate = trainerAttendance.getTrainingUnit().getDate();
        Trainer trainer = trainerAttendance.getTrainer();
        assertMonthlyReportEditable(trainer, unitDate);

        trainerAttendance.setPresent(true);
        trainerAttendanceRepository.save(trainerAttendance);

        TrainerMonthlyReport monthlyReport = getOrCreateMonthlyReport(
                trainer, unitDate.getYear(), unitDate.getMonthValue()
        );

        TrainerReport trainerReport = new TrainerReport(
                unitDate,
                null,
                trainerAttendance.getTrainingUnit().getTrainingSchedule().getTraining().getSchool().getName(),
                unitDate.getDayOfWeek(),
                ReportCategory.TRAINING,
                id,
                trainerAttendance.getTrainingUnit().getTrainingSchedule().getStartTime(),
                trainerAttendance.getTrainingUnit().getTrainingSchedule().getEndTime(),
                calculateMoney(trainerAttendance)
        );
        trainerReport.setTrainerMonthlyReport(monthlyReport);

        trainerReportRepository.save(trainerReport);
        return trainerAttendanceMapper.toDto(trainerAttendance);
    }

    public int calculateMoney(TrainerAttendance trainerAttendance) {
        LocalTime start = trainerAttendance.getTrainingUnit().getTrainingSchedule().getStartTime();
        LocalTime end = trainerAttendance.getTrainingUnit().getTrainingSchedule().getEndTime();
        long minutes = ChronoUnit.MINUTES.between(start, end);
        if (minutes <= 60) {
            return trainerAttendance.getTrainer().getHourlyRate();
        }
        return (int) Math.round((minutes / 60.0) * trainerAttendance.getTrainer().getHourlyRate());
    }

    @Transactional
    public TrainerAttendanceDto markAbsent(Long id) {
        TrainerAttendance trainerAttendance = trainerAttendanceRepository.findById(id)
                .orElseThrow(() -> new AttendanceException("Attendance with ID " + id + " not found", HttpStatus.NOT_FOUND));

        if (!trainerAttendance.isPresent()) {
            return null;
        }

        assertMonthlyReportEditable(
                trainerAttendance.getTrainer(),
                trainerAttendance.getTrainingUnit().getDate()
        );

        trainerAttendance.setPresent(false);

        trainerReportRepository.deleteTrainerReport(
                trainerAttendance.getTrainer().getId(),
                trainerAttendance.getId(),
                ReportCategory.TRAINING
        );

        trainerAttendanceRepository.save(trainerAttendance);

        return trainerAttendanceMapper.toDto(trainerAttendance);
    }

    private void assertMonthlyReportEditable(Trainer trainer, LocalDate date) {
        trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(trainer.getId(), date.getYear(), date.getMonthValue())
                .ifPresent(monthlyReport -> {
                    if (monthlyReport.getStatus() == MonthlyReportStatus.PAID) {
                        throw new AttendanceException(
                                "Výkaz za tento měsíc je již vyplacen, docházku nelze změnit.",
                                HttpStatus.BAD_REQUEST
                        );
                    }
                });
    }

    private TrainerMonthlyReport getOrCreateMonthlyReport(Trainer trainer, int year, int month) {
        return trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(trainer.getId(), year, month)
                .orElseGet(() -> trainerMonthlyReportRepository.save(
                        new TrainerMonthlyReport(trainer, year, month)
                ));
    }
}
