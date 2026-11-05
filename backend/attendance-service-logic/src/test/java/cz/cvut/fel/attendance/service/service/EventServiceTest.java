package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.mappers.ChildEventAttendanceMapper;
import cz.cvut.fel.attendance.service.mappers.EventMapper;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.repository.ChildEventAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.ChildRepository;
import cz.cvut.fel.attendance.service.repository.EventRepository;
import cz.cvut.fel.attendance.service.repository.TrainerMonthlyReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.fel.cvut.attendance.service.model.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    private EventRepository eventRepository;
    private ChildRepository childRepository;
    private TrainerRepository trainerRepository;
    private TrainerReportRepository trainerReportRepository;
    private ChildEventAttendanceRepository childEventAttendanceRepository;
    private TrainerMonthlyReportRepository trainerMonthlyReportRepository;

    private ChildEventAttendanceMapper childEventAttendanceMapper;
    private TrainerMapper trainerMapper;
    private EventMapper eventMapper;

    private MailService mailService;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        childRepository = Mockito.mock(ChildRepository.class);
        trainerRepository = Mockito.mock(TrainerRepository.class);
        trainerReportRepository = Mockito.mock(TrainerReportRepository.class);
        childEventAttendanceRepository = Mockito.mock(ChildEventAttendanceRepository.class);
        trainerMonthlyReportRepository = Mockito.mock(TrainerMonthlyReportRepository.class);

        childEventAttendanceMapper = Mockito.mock(ChildEventAttendanceMapper.class);
        trainerMapper = Mockito.mock(TrainerMapper.class);
        eventMapper = Mockito.mock(EventMapper.class);

        mailService = Mockito.mock(MailService.class);

        eventService = new EventService(
                eventRepository,
                childRepository,
                trainerRepository,
                trainerReportRepository,
                childEventAttendanceRepository,
                trainerMonthlyReportRepository,
                childEventAttendanceMapper,
                trainerMapper,
                eventMapper,
                mailService
        );
    }

    private static Trainer newTrainer(Long id) {
        Trainer trainer = new Trainer("Jana", "Trenérová", "trainer" + id + "@example.com", "60000000" + id, "pw", 300);
        trainer.setId(id);
        return trainer;
    }

    private static Event newEvent(Long id, LocalDate startDate, LocalDate endDate, int trainerSalary) {
        Event event = new Event();
        event.setId(id);
        event.setName("Letní soustředění");
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setLocation("Plzeň");
        event.setStartTime(LocalTime.of(9, 0));
        event.setEndTime(LocalTime.of(17, 0));
        event.setPlaces(20);
        event.setPrice(1000);
        event.setTrainerSalary(trainerSalary);
        event.setEventType(ReportCategory.EVENT);
        return event;
    }

    @Test
    void addTrainer_toFutureEvent_createsMonthlyReportInAdvanceForThatFutureMonth() {
        LocalDate futureDate = LocalDate.now().plusMonths(3);
        Event event = newEvent(10L, futureDate, null, 500);
        Trainer trainer = newTrainer(20L);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(trainerRepository.findById(20L)).thenReturn(Optional.of(trainer));
        when(trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(
                20L, futureDate.getYear(), futureDate.getMonthValue()))
                .thenReturn(Optional.empty());
        when(trainerMonthlyReportRepository.save(any(TrainerMonthlyReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainerReportRepository.save(any(TrainerReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        eventService.addTrainer(10L, 20L);
        assertThat(trainer.getEvents()).contains(event);
        assertThat(event.getTrainers()).contains(trainer);

        ArgumentCaptor<TrainerMonthlyReport> monthlyReportCaptor = ArgumentCaptor.forClass(TrainerMonthlyReport.class);
        verify(trainerMonthlyReportRepository).save(monthlyReportCaptor.capture());
        TrainerMonthlyReport createdMonthlyReport = monthlyReportCaptor.getValue();

        assertThat(createdMonthlyReport.getYear()).isEqualTo(futureDate.getYear());
        assertThat(createdMonthlyReport.getMonth()).isEqualTo(futureDate.getMonthValue());
        assertThat(createdMonthlyReport.getTrainer()).isEqualTo(trainer);
        assertThat(createdMonthlyReport.getStatus()).isEqualTo(MonthlyReportStatus.PENDING);
        assertThat(YearMonth.of(createdMonthlyReport.getYear(), createdMonthlyReport.getMonth()))
                .isGreaterThan(YearMonth.now());

        ArgumentCaptor<TrainerReport> reportCaptor = ArgumentCaptor.forClass(TrainerReport.class);
        verify(trainerReportRepository).save(reportCaptor.capture());
        TrainerReport createdReport = reportCaptor.getValue();

        assertThat(createdReport.getSourceId()).isEqualTo(10L);
        assertThat(createdReport.getReportCategory()).isEqualTo(ReportCategory.EVENT);
        assertThat(createdReport.getMoney()).isEqualTo(500);
        assertThat(createdReport.getStartDate()).isEqualTo(futureDate);
        assertThat(createdReport.getTrainerMonthlyReport()).isSameAs(createdMonthlyReport);
    }

    @Test
    void addTrainer_toEventInAMonthThatAlreadyHasAReport_reusesTheExistingMonthlyReport() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        Event event = newEvent(11L, futureDate, null, 400);
        Trainer trainer = newTrainer(21L);

        TrainerMonthlyReport existingMonthlyReport =
                new TrainerMonthlyReport(trainer, futureDate.getYear(), futureDate.getMonthValue());

        when(eventRepository.findById(11L)).thenReturn(Optional.of(event));
        when(trainerRepository.findById(21L)).thenReturn(Optional.of(trainer));
        when(trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(
                21L, futureDate.getYear(), futureDate.getMonthValue()))
                .thenReturn(Optional.of(existingMonthlyReport));
        when(trainerReportRepository.save(any(TrainerReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        eventService.addTrainer(11L, 21L);
        verify(trainerMonthlyReportRepository, never()).save(any());

        ArgumentCaptor<TrainerReport> reportCaptor = ArgumentCaptor.forClass(TrainerReport.class);
        verify(trainerReportRepository).save(reportCaptor.capture());
        assertThat(reportCaptor.getValue().getTrainerMonthlyReport()).isSameAs(existingMonthlyReport);
    }

    @Test
    void updateEvent_dateMovedToNewMonth_pendingReport_isRefiledAndFieldsAreUpdated() {
        LocalDate oldDate = LocalDate.of(2026, 1, 10);
        LocalDate newDate = LocalDate.of(2026, 4, 5);

        Event event = newEvent(30L, oldDate, null, 500);
        Trainer trainer = newTrainer(40L);
        TrainerMonthlyReport oldMonthlyReport = new TrainerMonthlyReport(trainer, 2026, 1);
        oldMonthlyReport.setStatus(MonthlyReportStatus.PENDING);

        TrainerReport report = new TrainerReport(
                oldDate, null, "Letní soustředění", oldDate.getDayOfWeek(),
                ReportCategory.EVENT, 30L, event.getStartTime(), event.getEndTime(), 500
        );
        report.setTrainerMonthlyReport(oldMonthlyReport);

        EventDto updateDto = new EventDto(
                30L, "Letní soustředění - posunuto", newDate, null, "Jiná hala",
                LocalTime.of(10, 0), LocalTime.of(18, 0),
                20, 0, 1000, "desc", 700, "Akce", null,
                List.of(), List.of(), List.of()
        );

        when(eventRepository.findById(30L)).thenReturn(Optional.of(event));
        mockUpdateEventFromDtoToMutateEvent();
        when(trainerReportRepository.findAllBySourceIdAndReportCategory(30L, ReportCategory.EVENT))
                .thenReturn(new ArrayList<>(List.of(report)));
        when(trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(40L, 2026, 4))
                .thenReturn(Optional.empty());
        when(trainerMonthlyReportRepository.save(any(TrainerMonthlyReport.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(trainerReportRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.updateEvent(30L, updateDto);

        assertThat(report.getStartDate()).isEqualTo(newDate);
        assertThat(report.getName()).isEqualTo("Letní soustředění - posunuto");
        assertThat(report.getMoney()).isEqualTo(700);
        assertThat(report.getTrainerMonthlyReport()).isNotSameAs(oldMonthlyReport);
        assertThat(report.getTrainerMonthlyReport().getYear()).isEqualTo(2026);
        assertThat(report.getTrainerMonthlyReport().getMonth()).isEqualTo(4);

        verify(trainerReportRepository).saveAll(List.of(report));
    }

    @Test
    void updateEvent_dateChanged_approvedButNotPaidReport_isStillUpdated() {
        LocalDate oldDate = LocalDate.of(2026, 1, 10);
        LocalDate newDate = LocalDate.of(2026, 1, 20);

        Event event = newEvent(31L, oldDate, null, 500);
        Trainer trainer = newTrainer(41L);
        TrainerMonthlyReport monthlyReport = new TrainerMonthlyReport(trainer, 2026, 1);
        monthlyReport.setStatus(MonthlyReportStatus.APPROVED);

        TrainerReport report = new TrainerReport(
                oldDate, null, "Akce", oldDate.getDayOfWeek(),
                ReportCategory.EVENT, 31L, event.getStartTime(), event.getEndTime(), 500
        );
        report.setTrainerMonthlyReport(monthlyReport);

        EventDto updateDto = new EventDto(
                31L, "Akce upravená", newDate, null, "Hala",
                LocalTime.of(10, 0), LocalTime.of(18, 0),
                20, 0, 1000, "desc", 650, "Akce", null,
                List.of(), List.of(), List.of()
        );

        when(eventRepository.findById(31L)).thenReturn(Optional.of(event));
        mockUpdateEventFromDtoToMutateEvent();
        when(trainerReportRepository.findAllBySourceIdAndReportCategory(31L, ReportCategory.EVENT))
                .thenReturn(new ArrayList<>(List.of(report)));
        when(trainerReportRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.updateEvent(31L, updateDto);

        assertThat(report.getStartDate()).isEqualTo(newDate);
        assertThat(report.getName()).isEqualTo("Akce upravená");
        assertThat(report.getMoney()).isEqualTo(650);
        assertThat(report.getTrainerMonthlyReport()).isSameAs(monthlyReport);
    }

    @Test
    void updateEvent_dateChanged_paidReport_isNeverModified() {
        LocalDate oldDate = LocalDate.of(2026, 1, 10);
        LocalDate newDate = LocalDate.of(2026, 5, 1);

        Event event = newEvent(32L, oldDate, null, 500);
        Trainer trainer = newTrainer(42L);
        TrainerMonthlyReport paidMonthlyReport = new TrainerMonthlyReport(trainer, 2026, 1);
        paidMonthlyReport.setStatus(MonthlyReportStatus.PAID);

        TrainerReport report = new TrainerReport(
                oldDate, null, "Akce", oldDate.getDayOfWeek(),
                ReportCategory.EVENT, 32L, event.getStartTime(), event.getEndTime(), 500
        );
        report.setTrainerMonthlyReport(paidMonthlyReport);

        EventDto updateDto = new EventDto(
                32L, "Akce - měla by se nezměnit", newDate, null, "Jiná hala",
                LocalTime.of(10, 0), LocalTime.of(18, 0),
                20, 0, 1000, "desc", 999, "Akce", null,
                List.of(), List.of(), List.of()
        );

        when(eventRepository.findById(32L)).thenReturn(Optional.of(event));
        mockUpdateEventFromDtoToMutateEvent();
        when(trainerReportRepository.findAllBySourceIdAndReportCategory(32L, ReportCategory.EVENT))
                .thenReturn(new ArrayList<>(List.of(report)));
        when(trainerReportRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.updateEvent(32L, updateDto);
        assertThat(event.getStartDate()).isEqualTo(newDate);
        assertThat(report.getStartDate()).isEqualTo(oldDate);
        assertThat(report.getName()).isEqualTo("Akce");
        assertThat(report.getMoney()).isEqualTo(500);
        assertThat(report.getTrainerMonthlyReport()).isSameAs(paidMonthlyReport);
        verify(trainerMonthlyReportRepository, never()).findByTrainerIdAndYearAndMonth(anyLong(), anyInt(), anyInt());
        verify(trainerMonthlyReportRepository, never()).save(any());
    }

    @Test
    void removeTrainer_delegatesDeletionToTheStatusGuardedRepositoryQuery() {
        Event event = newEvent(50L, LocalDate.now(), null, 500);
        Trainer trainer = newTrainer(60L);
        trainer.getEvents().add(event);
        event.getTrainers().add(trainer);

        when(eventRepository.findById(50L)).thenReturn(Optional.of(event));
        when(trainerRepository.findById(60L)).thenReturn(Optional.of(trainer));

        eventService.removeTrainer(50L, 60L);

        verify(trainerReportRepository, times(1))
                .deleteTrainerReport(60L, 50L, ReportCategory.EVENT);
        assertThat(trainer.getEvents()).doesNotContain(event);
        assertThat(event.getTrainers()).doesNotContain(trainer);
    }

    @Test
    void deleteEvent_delegatesDeletionToTheStatusGuardedRepositoryQuery() {
        Event event = newEvent(51L, LocalDate.now(), null, 500);
        Trainer trainer = newTrainer(61L);
        trainer.getEvents().add(event);
        event.getTrainers().add(trainer);

        when(eventRepository.findById(51L)).thenReturn(Optional.of(event));

        eventService.deleteEvent(51L);

        verify(trainerReportRepository, times(1))
                .deleteAllBySourceIdAndReportCategory(51L, ReportCategory.EVENT);
        verify(eventRepository, times(1)).delete(event);
    }

    private void mockUpdateEventFromDtoToMutateEvent() {
        Mockito.doAnswer(invocation -> {
            EventDto dto = invocation.getArgument(0);
            Event target = invocation.getArgument(1);
            target.setName(dto.name());
            target.setStartDate(dto.startDate());
            target.setEndDate(dto.endDate());
            target.setLocation(dto.location());
            target.setStartTime(dto.startTime());
            target.setEndTime(dto.endTime());
            target.setPlaces(dto.places());
            target.setPrice(dto.price());
            target.setDescription(dto.description());
            target.setTrainerSalary(dto.trainerSalary());
            return null;
        }).when(eventMapper).updateEventFromDto(any(EventDto.class), any(Event.class));
    }
}
