package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.EventRepository;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerMonthlyReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.trainer.CreateReportItemDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerMonthlyReportDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateReportItemMoneyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrainerServiceTest {

    private UserRepository userRepository;
    private TrainingUnitRepository trainingUnitRepository;
    private TrainerRepository trainerRepository;
    private EventRepository eventRepository;
    private TrainerMonthlyReportRepository trainerMonthlyReportRepository;
    private TrainerReportRepository trainerReportRepository;
    private TrainerAttendanceRepository trainerAttendanceRepository;
    private TrainingUnitMapper trainingUnitMapper;
    private TrainerMapper trainerMapper;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        trainingUnitRepository = Mockito.mock(TrainingUnitRepository.class);
        trainerRepository = Mockito.mock(TrainerRepository.class);
        eventRepository = Mockito.mock(EventRepository.class);
        trainerMonthlyReportRepository = Mockito.mock(TrainerMonthlyReportRepository.class);
        trainerReportRepository = Mockito.mock(TrainerReportRepository.class);
        trainerAttendanceRepository = Mockito.mock(TrainerAttendanceRepository.class);
        trainingUnitMapper = Mockito.mock(TrainingUnitMapper.class);
        trainerMapper = Mockito.mock(TrainerMapper.class);

        trainerService = new TrainerService(
                userRepository,
                trainingUnitRepository,
                trainerRepository,
                eventRepository,
                trainerMonthlyReportRepository,
                trainerReportRepository,
                trainerAttendanceRepository,
                trainingUnitMapper,
                trainerMapper
        );
    }

    private static TrainerMonthlyReport monthlyReport(long id, int year, int month, MonthlyReportStatus status) {
        Trainer trainer = new Trainer("Petr", "Trenér", "p" + id + "@example.com", "70000000" + id, "pw", 300);
        trainer.setId(id);
        TrainerMonthlyReport report = new TrainerMonthlyReport(trainer, year, month);
        report.setId(id);
        report.setStatus(status);
        return report;
    }

    private static CreateReportItemDto manualItemDto(LocalDate date) {
        return new CreateReportItemDto(date, null, "Lekce navíc", "OTHER", 300, null, null);
    }

    @Test
    void addReportItem_pendingReport_addsItemSuccessfully() {
        TrainerMonthlyReport report = monthlyReport(1, 2026, 3, MonthlyReportStatus.PENDING);
        when(trainerMonthlyReportRepository.findById(1L)).thenReturn(Optional.of(report));
        when(trainerReportRepository.save(any(TrainerReport.class))).thenAnswer(inv -> inv.getArgument(0));

        trainerService.addReportItem(1L, manualItemDto(LocalDate.of(2026, 3, 10)));

        assertThat(report.getReports()).hasSize(1);
        TrainerReport added = report.getReports().get(0);
        assertThat(added.getMoney()).isEqualTo(300);
        assertThat(added.getName()).isEqualTo("Lekce navíc");
        assertThat(added.getDayOfWeek()).isEqualTo(LocalDate.of(2026, 3, 10).getDayOfWeek());
        verify(trainerReportRepository).save(any(TrainerReport.class));
    }

    @Test
    void addReportItem_approvedReport_stillAddsItemSuccessfully() {
        TrainerMonthlyReport report = monthlyReport(2, 2026, 3, MonthlyReportStatus.APPROVED);
        when(trainerMonthlyReportRepository.findById(2L)).thenReturn(Optional.of(report));
        when(trainerReportRepository.save(any(TrainerReport.class))).thenAnswer(inv -> inv.getArgument(0));

        trainerService.addReportItem(2L, manualItemDto(LocalDate.of(2026, 3, 12)));

        assertThat(report.getReports()).hasSize(1);
    }

    @Test
    void addReportItem_paidReport_throwsAndNothingIsAdded() {
        TrainerMonthlyReport report = monthlyReport(3, 2026, 3, MonthlyReportStatus.PAID);
        when(trainerMonthlyReportRepository.findById(3L)).thenReturn(Optional.of(report));

        assertThatThrownBy(() -> trainerService.addReportItem(3L, manualItemDto(LocalDate.of(2026, 3, 12))))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Vyplacený výkaz nelze upravovat");

        assertThat(report.getReports()).isEmpty();
        verify(trainerReportRepository, never()).save(any());
    }

    @Test
    void updateReportItemMoney_pendingReport_updatesMoney() {
        TrainerMonthlyReport report = monthlyReport(4, 2026, 3, MonthlyReportStatus.PENDING);
        TrainerReport item = new TrainerReport();
        item.setId(5L);
        item.setMoney(100);
        item.setTrainerMonthlyReport(report);
        when(trainerReportRepository.findById(5L)).thenReturn(Optional.of(item));

        trainerService.updateReportItemMoney(5L, new UpdateReportItemMoneyDto(250));

        assertThat(item.getMoney()).isEqualTo(250);
        verify(trainerReportRepository).save(item);
    }

    @Test
    void updateReportItemMoney_approvedReport_stillUpdatesMoney() {
        TrainerMonthlyReport report = monthlyReport(6, 2026, 3, MonthlyReportStatus.APPROVED);
        TrainerReport item = new TrainerReport();
        item.setId(7L);
        item.setMoney(100);
        item.setTrainerMonthlyReport(report);
        when(trainerReportRepository.findById(7L)).thenReturn(Optional.of(item));

        trainerService.updateReportItemMoney(7L, new UpdateReportItemMoneyDto(250));

        assertThat(item.getMoney()).isEqualTo(250);
    }

    @Test
    void updateReportItemMoney_paidReport_throwsAndMoneyIsUnchanged() {
        TrainerMonthlyReport report = monthlyReport(8, 2026, 3, MonthlyReportStatus.PAID);
        TrainerReport item = new TrainerReport();
        item.setId(9L);
        item.setMoney(100);
        item.setTrainerMonthlyReport(report);
        when(trainerReportRepository.findById(9L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> trainerService.updateReportItemMoney(9L, new UpdateReportItemMoneyDto(250)))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Vyplacený výkaz nelze upravovat");

        assertThat(item.getMoney()).isEqualTo(100);
        verify(trainerReportRepository, never()).save(any());
    }

    @Test
    void deleteReportItem_pendingManualItem_deletesItAndLeavesAttendanceUntouched() {
        TrainerMonthlyReport report = monthlyReport(10, 2026, 3, MonthlyReportStatus.PENDING);
        TrainerReport item = new TrainerReport();
        item.setId(11L);
        item.setSourceId(null);
        item.setReportCategory(ReportCategory.OTHER);
        item.setTrainerMonthlyReport(report);
        report.getReports().add(item);

        when(trainerReportRepository.findById(11L)).thenReturn(Optional.of(item));

        trainerService.deleteReportItem(11L);

        verify(trainerReportRepository).delete(item);
        assertThat(report.getReports()).doesNotContain(item);
        verify(trainerAttendanceRepository, never()).findById(any());
    }

    @Test
    void deleteReportItem_approvedTrainingAttendanceItem_deletesAndRevertsAttendanceToAbsent() {
        TrainerMonthlyReport report = monthlyReport(12, 2026, 3, MonthlyReportStatus.APPROVED);
        TrainerReport item = new TrainerReport();
        item.setId(13L);
        item.setSourceId(55L);
        item.setReportCategory(ReportCategory.TRAINING);
        item.setTrainerMonthlyReport(report);
        report.getReports().add(item);

        TrainerAttendance attendance = new TrainerAttendance();
        attendance.setId(55L);
        attendance.setPresent(true);

        when(trainerReportRepository.findById(13L)).thenReturn(Optional.of(item));
        when(trainerAttendanceRepository.findById(55L)).thenReturn(Optional.of(attendance));

        trainerService.deleteReportItem(13L);

        verify(trainerReportRepository).delete(item);
        assertThat(attendance.isPresent()).isFalse();
        verify(trainerAttendanceRepository).save(attendance);
    }

    @Test
    void deleteReportItem_paidReport_throwsAndItemSurvives() {
        TrainerMonthlyReport report = monthlyReport(14, 2026, 3, MonthlyReportStatus.PAID);
        TrainerReport item = new TrainerReport();
        item.setId(15L);
        item.setSourceId(null);
        item.setReportCategory(ReportCategory.OTHER);
        item.setTrainerMonthlyReport(report);
        report.getReports().add(item);

        when(trainerReportRepository.findById(15L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> trainerService.deleteReportItem(15L))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Vyplacený výkaz nelze upravovat");

        assertThat(report.getReports()).contains(item);
        verify(trainerReportRepository, never()).delete(any());
    }

    @Test
    void getMonthlyReports_includesAFutureDatedMonthlyReportCreatedInAdvance() {
        Trainer trainer = new Trainer("Petr", "Trenér", "future@example.com", "000000000", "test-only-password-hash", 300);
        trainer.setId(99L);

        LocalDate today = LocalDate.now();
        YearMonth futureMonth = YearMonth.from(today).plusMonths(2);

        TrainerMonthlyReport currentReport = new TrainerMonthlyReport(trainer, today.getYear(), today.getMonthValue());
        TrainerMonthlyReport futureReport = new TrainerMonthlyReport(trainer, futureMonth.getYear(), futureMonth.getMonthValue());

        when(userRepository.findByEmail("future@example.com")).thenReturn(Optional.of((User) trainer));
        when(trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(99L, today.getYear(), today.getMonthValue()))
                .thenReturn(Optional.of(currentReport));
        when(trainerMonthlyReportRepository.findAllByTrainerEmailOrderedDesc("future@example.com"))
                .thenReturn(List.of(futureReport, currentReport));

        List<TrainerMonthlyReportDto> result = trainerService.getMonthlyReports("future@example.com");

        assertThat(result)
                .anySatisfy(dto -> {
                    assertThat(dto.year()).isEqualTo(futureMonth.getYear());
                    assertThat(dto.month()).isEqualTo(futureMonth.getMonthValue());
                });
        assertThat(YearMonth.of(futureMonth.getYear(), futureMonth.getMonthValue())).isGreaterThan(YearMonth.now());
    }
}
