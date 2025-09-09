package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.ChildTrainingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildAttendanceRepository extends JpaRepository<ChildTrainingAttendance, Long> {
}
