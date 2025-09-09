package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerAttendanceRepository extends JpaRepository<TrainerAttendance, Long> {
}
