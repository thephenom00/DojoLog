package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface TrainerReportRepository extends JpaRepository<TrainerReport, Long> {
    @Modifying
    @Query("""
    DELETE from TrainerReport tr
    WHERE tr.trainerMonthlyReport.trainer.id = :trainerId
      AND tr.sourceId = :sourceId
      AND tr.reportCategory = :reportCategory
      AND tr.trainerMonthlyReport.status <> cz.cvut.fel.attendance.service.enums.MonthlyReportStatus.PAID
    """)
    void deleteTrainerReport(@Param("trainerId") Long trainerId, @Param("sourceId") Long sourceId, @Param("reportCategory") ReportCategory reportCategory);

    @Modifying
    @Query("""
    DELETE from TrainerReport tr
    WHERE tr.sourceId = :sourceId
      AND tr.reportCategory = :reportCategory
      AND tr.trainerMonthlyReport.status <> cz.cvut.fel.attendance.service.enums.MonthlyReportStatus.PAID
    """)
    void deleteAllBySourceIdAndReportCategory(@Param("sourceId") Long sourceId, @Param("reportCategory") ReportCategory reportCategory);

    List<TrainerReport> findAllBySourceIdAndReportCategory(Long sourceId, ReportCategory reportCategory);

    @Modifying
    @Query("""
    DELETE from TrainerReport tr
    WHERE tr.sourceId IN :sourceIds
      AND tr.reportCategory = :reportCategory
      AND tr.trainerMonthlyReport.status <> cz.cvut.fel.attendance.service.enums.MonthlyReportStatus.PAID
    """)
    void deleteAllBySourceIdInAndReportCategory(@Param("sourceIds") java.util.Collection<Long> sourceIds, @Param("reportCategory") ReportCategory reportCategory);
}
