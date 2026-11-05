package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrainerReportRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainerMonthlyReportRepository trainerMonthlyReportRepository;

    @Autowired
    private TrainerReportRepository trainerReportRepository;

    @Autowired
    private TestEntityManager entityManager;

    private static int trainerCounter = 0;

    private Trainer persistTrainer() {
        int n = ++trainerCounter;
        Trainer trainer = new Trainer(
                "Trenér", "Test" + n, "trainer" + n + "@example.com", "70000000" + n, "password", 300
        );
        return trainerRepository.save(trainer);
    }

    private TrainerMonthlyReport persistMonthlyReport(Trainer trainer, int year, int month, MonthlyReportStatus status) {
        TrainerMonthlyReport report = new TrainerMonthlyReport(trainer, year, month);
        report.setStatus(status);
        return trainerMonthlyReportRepository.save(report);
    }

    private TrainerReport persistTrainerReport(TrainerMonthlyReport monthlyReport, Long sourceId, ReportCategory category) {
        TrainerReport report = new TrainerReport(
                LocalDate.of(monthlyReport.getYear(), monthlyReport.getMonth(), 1),
                null,
                "Akce",
                null,
                category,
                sourceId,
                null,
                null,
                500
        );
        report.setTrainerMonthlyReport(monthlyReport);
        return trainerReportRepository.save(report);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void deleteTrainerReport_pendingReport_rowIsDeleted() {
        Trainer trainer = persistTrainer();
        TrainerMonthlyReport monthlyReport = persistMonthlyReport(trainer, 2026, 3, MonthlyReportStatus.PENDING);
        TrainerReport report = persistTrainerReport(monthlyReport, 900L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteTrainerReport(trainer.getId(), 900L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(report.getId())).isEmpty();
    }

    @Test
    void deleteTrainerReport_approvedReport_rowIsDeleted() {
        Trainer trainer = persistTrainer();
        TrainerMonthlyReport monthlyReport = persistMonthlyReport(trainer, 2026, 3, MonthlyReportStatus.APPROVED);
        TrainerReport report = persistTrainerReport(monthlyReport, 901L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteTrainerReport(trainer.getId(), 901L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(report.getId())).isEmpty();
    }

    @Test
    void deleteTrainerReport_paidReport_rowSurvives() {
        Trainer trainer = persistTrainer();
        TrainerMonthlyReport monthlyReport = persistMonthlyReport(trainer, 2026, 3, MonthlyReportStatus.PAID);
        TrainerReport report = persistTrainerReport(monthlyReport, 902L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteTrainerReport(trainer.getId(), 902L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(report.getId())).isPresent();
    }

    @Test
    void deleteAllBySourceIdAndReportCategory_pendingReport_rowIsDeleted() {
        Trainer trainer = persistTrainer();
        TrainerMonthlyReport monthlyReport = persistMonthlyReport(trainer, 2026, 4, MonthlyReportStatus.PENDING);
        TrainerReport report = persistTrainerReport(monthlyReport, 910L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteAllBySourceIdAndReportCategory(910L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(report.getId())).isEmpty();
    }

    @Test
    void deleteAllBySourceIdAndReportCategory_onlyPaidReportSurvivesEvenAcrossMultipleTrainers() {
        Trainer trainerA = persistTrainer();
        Trainer trainerB = persistTrainer();
        TrainerMonthlyReport approvedReport = persistMonthlyReport(trainerA, 2026, 4, MonthlyReportStatus.APPROVED);
        TrainerMonthlyReport paidReport = persistMonthlyReport(trainerB, 2026, 4, MonthlyReportStatus.PAID);
        TrainerReport reportA = persistTrainerReport(approvedReport, 911L, ReportCategory.EVENT);
        TrainerReport reportB = persistTrainerReport(paidReport, 911L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteAllBySourceIdAndReportCategory(911L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(reportA.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(reportB.getId())).isPresent();
    }

    @Test
    void deleteAllBySourceIdAndReportCategory_mixOfStatuses_onlyThePaidOneSurvives() {
        Trainer trainerPending = persistTrainer();
        Trainer trainerApproved = persistTrainer();
        Trainer trainerPaid = persistTrainer();
        TrainerMonthlyReport pendingMonthly = persistMonthlyReport(trainerPending, 2026, 5, MonthlyReportStatus.PENDING);
        TrainerMonthlyReport approvedMonthly = persistMonthlyReport(trainerApproved, 2026, 5, MonthlyReportStatus.APPROVED);
        TrainerMonthlyReport paidMonthly = persistMonthlyReport(trainerPaid, 2026, 5, MonthlyReportStatus.PAID);
        TrainerReport pendingReport = persistTrainerReport(pendingMonthly, 920L, ReportCategory.EVENT);
        TrainerReport approvedReport = persistTrainerReport(approvedMonthly, 920L, ReportCategory.EVENT);
        TrainerReport paidReport = persistTrainerReport(paidMonthly, 920L, ReportCategory.EVENT);
        flushAndClear();

        trainerReportRepository.deleteAllBySourceIdAndReportCategory(920L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(pendingReport.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(approvedReport.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(paidReport.getId())).isPresent();
    }

    @Test
    void deleteAllBySourceIdInAndReportCategory_mixedStatusesInOneBatch_onlyThePaidRowSurvives() {
        Trainer trainerPending = persistTrainer();
        Trainer trainerApproved = persistTrainer();
        Trainer trainerPaid = persistTrainer();
        TrainerMonthlyReport pendingMonthly = persistMonthlyReport(trainerPending, 2026, 6, MonthlyReportStatus.PENDING);
        TrainerMonthlyReport approvedMonthly = persistMonthlyReport(trainerApproved, 2026, 6, MonthlyReportStatus.APPROVED);
        TrainerMonthlyReport paidMonthly = persistMonthlyReport(trainerPaid, 2026, 6, MonthlyReportStatus.PAID);
        TrainerReport pendingAttendanceReport = persistTrainerReport(pendingMonthly, 930L, ReportCategory.TRAINING);
        TrainerReport approvedAttendanceReport = persistTrainerReport(approvedMonthly, 931L, ReportCategory.TRAINING);
        TrainerReport paidAttendanceReport = persistTrainerReport(paidMonthly, 932L, ReportCategory.TRAINING);
        flushAndClear();

        trainerReportRepository.deleteAllBySourceIdInAndReportCategory(
                List.of(930L, 931L, 932L), ReportCategory.TRAINING
        );
        flushAndClear();

        assertThat(trainerReportRepository.findById(pendingAttendanceReport.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(approvedAttendanceReport.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(paidAttendanceReport.getId())).isPresent();
    }

    @Test
    void deleteTrainerReport_doesNotTouchRowsForADifferentSourceIdOrCategory() {
        Trainer trainer = persistTrainer();
        TrainerMonthlyReport monthlyReport = persistMonthlyReport(trainer, 2026, 7, MonthlyReportStatus.PENDING);
        TrainerReport targetReport = persistTrainerReport(monthlyReport, 940L, ReportCategory.EVENT);
        TrainerReport otherSourceReport = persistTrainerReport(monthlyReport, 941L, ReportCategory.EVENT);
        TrainerReport otherCategoryReport = persistTrainerReport(monthlyReport, 940L, ReportCategory.TRAINING);
        flushAndClear();

        trainerReportRepository.deleteTrainerReport(trainer.getId(), 940L, ReportCategory.EVENT);
        flushAndClear();

        assertThat(trainerReportRepository.findById(targetReport.getId())).isEmpty();
        assertThat(trainerReportRepository.findById(otherSourceReport.getId())).isPresent();
        assertThat(trainerReportRepository.findById(otherCategoryReport.getId())).isPresent();
    }
}
