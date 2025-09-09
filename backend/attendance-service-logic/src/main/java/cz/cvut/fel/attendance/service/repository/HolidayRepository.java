package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Holiday;
import cz.cvut.fel.attendance.service.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    boolean existsHolidayBySchoolAndAndNameAndStartDateAndEndDate(School school, String name, LocalDate startDate, LocalDate endDate);
}
