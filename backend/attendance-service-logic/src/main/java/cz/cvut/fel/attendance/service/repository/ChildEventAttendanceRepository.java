package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.ChildEventAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChildEventAttendanceRepository extends JpaRepository<ChildEventAttendance, Long> {
    ChildEventAttendance findByChildIdAndEventId(Long childId, Long eventId);
}
