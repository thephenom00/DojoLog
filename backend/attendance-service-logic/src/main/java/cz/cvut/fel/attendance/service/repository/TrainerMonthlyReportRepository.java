package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerMonthlyReportRepository extends JpaRepository<TrainerMonthlyReport, Long> {
    Optional<TrainerMonthlyReport> findByTrainerIdAndYearAndMonth(Long trainerId, int year, int month);

    @Query("""
        SELECT tmr FROM TrainerMonthlyReport tmr
        WHERE tmr.trainer.email = :email
        ORDER BY tmr.year DESC, tmr.month DESC
    """)
    List<TrainerMonthlyReport> findAllByTrainerEmailOrderedDesc(@Param("email") String email);
}
