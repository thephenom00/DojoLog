package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerAttendanceRepository extends JpaRepository<TrainerAttendance, Long> {
    boolean existsByTrainer_IdAndTrainingUnit_Id(Long trainerId, Long trainingUnitId);
}
