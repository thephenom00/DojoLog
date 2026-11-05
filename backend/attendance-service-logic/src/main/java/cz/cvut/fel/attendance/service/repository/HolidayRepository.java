package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Modifying
    @Query("""
        DELETE FROM Holiday h
        WHERE (h.endDate IS NOT NULL AND h.endDate < :today)
           OR (h.endDate IS NULL AND h.startDate < :today)
    """)
    void deleteOutdatedHolidays(@Param("today") LocalDate today);
}
