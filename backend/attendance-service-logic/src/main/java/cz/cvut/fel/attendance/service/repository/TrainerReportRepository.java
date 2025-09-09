package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainerReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface TrainerReportRepository extends JpaRepository<TrainerReport, Long> {
    List<TrainerReport> findAllByTrainerId(Long id);
    TrainerReport findByTrainerIdAndTrainerAttendanceId(Long trainerId, Long trainerAttendanceId);
    TrainerReport findByTrainerIdAndStartTimeAndEndTimeAndName(Long trainerId, LocalTime startTime, LocalTime endTime, String location);
}
