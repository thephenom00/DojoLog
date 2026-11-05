package cz.cvut.fel.attendance.service.service;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import cz.cvut.fel.attendance.service.enums.ReportCategory;
import cz.cvut.fel.attendance.service.mappers.TrainerMapper;
import cz.cvut.fel.attendance.service.mappers.TrainingUnitMapper;
import cz.cvut.fel.attendance.service.model.Event;
import cz.cvut.fel.attendance.service.model.Trainer;
import cz.cvut.fel.attendance.service.model.TrainerAttendance;
import cz.cvut.fel.attendance.service.model.TrainerMonthlyReport;
import cz.cvut.fel.attendance.service.model.TrainerReport;
import cz.cvut.fel.attendance.service.model.Training;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import cz.cvut.fel.attendance.service.model.User;
import cz.cvut.fel.attendance.service.repository.EventRepository;
import cz.cvut.fel.attendance.service.repository.TrainerAttendanceRepository;
import cz.cvut.fel.attendance.service.repository.TrainerMonthlyReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerReportRepository;
import cz.cvut.fel.attendance.service.repository.TrainerRepository;
import cz.cvut.fel.attendance.service.repository.TrainingUnitRepository;
import cz.cvut.fel.attendance.service.repository.UserRepository;
import cz.fel.cvut.attendance.service.exception.UserException;
import cz.fel.cvut.attendance.service.model.PagedTrainingUnitDto;
import cz.fel.cvut.attendance.service.model.TrainerDto;
import cz.fel.cvut.attendance.service.model.trainer.CreateReportItemDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerMonthlyReportDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportDto;
import cz.fel.cvut.attendance.service.model.trainer.TrainerReportItemDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateReportItemMoneyDto;
import cz.fel.cvut.attendance.service.model.trainer.UpdateTrainerReportNoteDto;
import cz.fel.cvut.attendance.service.model.TrainingUnitDto;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private static final DateTimeFormatter REPORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy");
    private static final DateTimeFormatter REPORT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

    private static final int REPORT_GRACE_PERIOD_DAYS = 7;

    private final UserRepository userRepository;
    private final TrainingUnitRepository trainingUnitRepository;
    private final TrainerRepository trainerRepository;
    private final EventRepository eventRepository;
    private final TrainerMonthlyReportRepository trainerMonthlyReportRepository;
    private final TrainerReportRepository trainerReportRepository;
    private final TrainerAttendanceRepository trainerAttendanceRepository;

    private final TrainingUnitMapper trainingUnitMapper;
    private final TrainerMapper trainerMapper;

    public List<TrainerDto> getTrainers() {
        return trainerMapper.toDtoList(trainerRepository.findByActiveTrue());
    }

    @Transactional
    public TrainerDto updateTrainer(Long id, TrainerDto trainerDto) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new UserException("Trainer with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        trainerMapper.updateTrainerFromDto(trainerDto, trainer);
        trainerRepository.save(trainer);

        return trainerMapper.toDto(trainer);
    }

    @Transactional
    public void deactivateTrainer(Long id) {
        Trainer trainer = trainerRepository.findById(id)
                .orElseThrow(() -> new UserException("Trainer with ID " + id + " not found.", HttpStatus.NOT_FOUND));

        for (Training training : new ArrayList<>(trainer.getTrainings())) {
            training.removeTrainer(trainer);
        }

        trainer.setActive(false);
        trainerRepository.save(trainer);
    }

    public List<TrainerReportDto> getCurrentMonthReport(String email) {
        Trainer trainer = (Trainer) userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");

        List<TrainerReportDto> reports = new ArrayList<>();
        reports.addAll(getAttendanceReport(trainer, now, formatter));
        reports.addAll(getEventReport(trainer, now, formatter));

        return reports;
    }

    private List<TrainerReportDto> getAttendanceReport(Trainer trainer, LocalDate now, DateTimeFormatter formatter) {
        List<TrainerAttendance> trainingAttendances = trainer.getTrainingAttendances();
        List<TrainerReportDto> reports = new ArrayList<>();
        Month currentMonth = now.getMonth();
        int currentYear = now.getYear();

        for (TrainerAttendance a : trainingAttendances) {
            LocalDate date = a.getTrainingUnit().getDate();
            if (date.getMonth() == currentMonth && date.getYear() == currentYear && a.isPresent()) {
                String school = a.getTrainingUnit().getTrainingSchedule().getTraining().getSchool().getName();
                LocalTime startTime = a.getTrainingUnit().getTrainingSchedule().getStartTime();
                LocalTime endTime = a.getTrainingUnit().getTrainingSchedule().getEndTime();
                DayOfWeek dayOfWeek = a.getTrainingUnit().getTrainingSchedule().getDayOfWeek();
                String formattedDate = date.format(formatter);
                double hours = Duration.between(startTime, endTime).toMinutes() / 60.0;
                hours = Math.round(hours * 10.0) / 10.0;

                String type = trainer.getTrainings().contains(a.getTrainingUnit().getTrainingSchedule().getTraining())
                        ? "Training" : "Substitution";

                reports.add(new TrainerReportDto(
                        formattedDate,
                        school,
                        dayOfWeek,
                        startTime,
                        endTime,
                        hours,
                        type,
                        trainer.getHourlyRate()
                ));
            }
        }
        return reports;
    }

    private List<TrainerReportDto> getEventReport(Trainer trainer, LocalDate now, DateTimeFormatter formatter) {
        List<Event> events = eventRepository.findAll();
        List<TrainerReportDto> reports = new ArrayList<>();
        Month currentMonth = now.getMonth();
        int currentYear = now.getYear();

        for (Event e : events) {
            if (e.getStartDate().getMonth() == currentMonth && e.getStartDate().getYear() == currentYear) {
                if (e.getTrainers().contains(trainer)) {
                    String formattedDate;
                    if (e.getEndDate() != null) {
                        formattedDate = e.getStartDate().format(formatter) + " - " + e.getEndDate().format(formatter);
                    } else {
                        formattedDate = e.getStartDate().format(formatter);
                    }

                    DayOfWeek dayOfWeek = (e.getEndDate() == null && e.getStartDate() != null)
                            ? e.getStartDate().getDayOfWeek()
                            : null;

                    reports.add(new TrainerReportDto(
                            formattedDate,
                            e.getName(),
                            dayOfWeek,
                            null,
                            null,
                            null,
                            e.getEventType() != null ? e.getEventType().getName() : null,
                            e.getTrainerSalary()
                    ));
                }
            }
        }
        return reports;
    }


    public List<TrainingUnitDto> getUpcomingTrainingUnits(String email) {
        List<TrainingUnit> upcomingUnits = trainingUnitRepository.findUpcomingUnitsByTrainerEmail(email);
        return upcomingUnits.stream()
                .map(trainingUnitMapper::toDto)
                .toList();
    }

    public PagedTrainingUnitDto getPastTrainingUnits(String email, int page, int size) {
        Page<TrainingUnit> pastPage = trainingUnitRepository
                .findPastUnitsByTrainerEmail(email, PageRequest.of(page, size));

        List<TrainingUnitDto> dtos = pastPage.getContent().stream()
                .map(trainingUnitMapper::toDto)
                .toList();

        return new PagedTrainingUnitDto(
                dtos,
                pastPage.getNumber(),
                pastPage.getTotalPages(),
                pastPage.getTotalElements()
        );
    }

    @Transactional
    public List<TrainerMonthlyReportDto> getMonthlyReports(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("Trainer not found.", HttpStatus.NOT_FOUND));
        if (!(user instanceof Trainer trainer)) {
            throw new UserException("User is not a trainer.", HttpStatus.BAD_REQUEST);
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Ensure current month report exists so it is always available
        trainerMonthlyReportRepository.findByTrainerIdAndYearAndMonth(trainer.getId(), currentYear, currentMonth)
                .orElseGet(() -> trainerMonthlyReportRepository.save(
                        new TrainerMonthlyReport(trainer, currentYear, currentMonth)
                ));

        return trainerMonthlyReportRepository.findAllByTrainerEmailOrderedDesc(email).stream()
                .filter(mr -> mr.getYear() < currentYear || (mr.getYear() == currentYear && mr.getMonth() <= currentMonth))
                .map(mr -> toMonthlyReportDto(mr, today))
                .toList();
    }

    @Transactional
    public TrainerMonthlyReportDto updateReportNote(String email, Long reportId, String note) {
        TrainerMonthlyReport monthlyReport = getEditableMonthlyReport(email, reportId);
        monthlyReport.setNote(note);
        trainerMonthlyReportRepository.save(monthlyReport);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    @Transactional
    public TrainerMonthlyReportDto approveReport(String email, Long reportId) {
        TrainerMonthlyReport monthlyReport = getEditableMonthlyReport(email, reportId);
        monthlyReport.setStatus(MonthlyReportStatus.APPROVED);
        monthlyReport.setApprovedAt(LocalDateTime.now());
        trainerMonthlyReportRepository.save(monthlyReport);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    @Transactional
    public TrainerMonthlyReportDto addReportItem(Long reportId, CreateReportItemDto dto) {
        TrainerMonthlyReport monthlyReport = trainerMonthlyReportRepository.findById(reportId)
                .orElseThrow(() -> new UserException("Report with ID " + reportId + " not found.", HttpStatus.NOT_FOUND));

        assertReportEditable(monthlyReport);

        ReportCategory category;
        try {
            category = ReportCategory.fromCzechName(dto.category());
        } catch (Exception e) {
            try {
                category = ReportCategory.valueOf(dto.category());
            } catch (Exception ex) {
                category = ReportCategory.OTHER;
            }
        }

        LocalDate date = dto.date() != null ? dto.date() : LocalDate.now();
        LocalDate endDate = dto.endDate();
        if (endDate != null && endDate.isBefore(date)) {
            throw new UserException("Datum konce musí být stejné nebo pozdější než datum začátku.", HttpStatus.BAD_REQUEST);
        }

        YearMonth reportMonth = YearMonth.of(monthlyReport.getYear(), monthlyReport.getMonth());
        YearMonth previousMonth = reportMonth.minusMonths(1);
        YearMonth nextMonth = reportMonth.plusMonths(1);

        YearMonth startMonth = YearMonth.from(date);
        if (startMonth.isBefore(previousMonth) || startMonth.isAfter(reportMonth)) {
            throw new UserException(
                    "Datum začátku musí spadat do vybraného měsíce nebo měsíce předchozího.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (endDate != null) {
            YearMonth endMonth = YearMonth.from(endDate);
            if (endMonth.isBefore(reportMonth) || endMonth.isAfter(nextMonth)) {
                throw new UserException(
                        "Datum konce musí spadat do vybraného měsíce nebo měsíce následujícího.",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        TrainerReport reportItem = new TrainerReport();
        reportItem.setStartDate(date);
        reportItem.setEndDate(endDate);
        reportItem.setName(dto.name() != null && !dto.name().isBlank() ? dto.name() : category.getName());
        reportItem.setDayOfWeek(
                (endDate == null || date.equals(endDate)) ? date.getDayOfWeek() : null
        );
        reportItem.setReportCategory(category);
        reportItem.setSourceId(null);
        reportItem.setMoney(dto.money());
        reportItem.setStartTime(dto.startTime());
        reportItem.setEndTime(dto.endTime());
        reportItem.setTrainerMonthlyReport(monthlyReport);

        monthlyReport.getReports().add(reportItem);
        trainerReportRepository.save(reportItem);
        trainerMonthlyReportRepository.save(monthlyReport);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    @Transactional
    public TrainerMonthlyReportDto updateReportItemMoney(Long itemId, UpdateReportItemMoneyDto moneyDto) {
        TrainerReport item = trainerReportRepository.findById(itemId)
                .orElseThrow(() -> new UserException("Item with ID " + itemId + " not found.", HttpStatus.NOT_FOUND));

        TrainerMonthlyReport monthlyReport = item.getTrainerMonthlyReport();
        assertReportEditable(monthlyReport);

        item.setMoney(moneyDto.money());
        trainerReportRepository.save(item);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    @Transactional
    public TrainerMonthlyReportDto deleteReportItem(Long itemId) {
        TrainerReport item = trainerReportRepository.findById(itemId)
                .orElseThrow(() -> new UserException("Item with ID " + itemId + " not found.", HttpStatus.NOT_FOUND));

        TrainerMonthlyReport monthlyReport = item.getTrainerMonthlyReport();
        assertReportEditable(monthlyReport);

        boolean isManual = item.getSourceId() == null;
        boolean isTrainingAttendance = !isManual && item.getReportCategory() == ReportCategory.TRAINING;

        if (!isManual && !isTrainingAttendance) {
            throw new UserException("Lze mazat pouze ručně přidané položky nebo záznamy docházky.", HttpStatus.BAD_REQUEST);
        }

        if (isTrainingAttendance) {
            trainerAttendanceRepository.findById(item.getSourceId()).ifPresent(attendance -> {
                attendance.setPresent(false);
                trainerAttendanceRepository.save(attendance);
            });
        }

        monthlyReport.getReports().remove(item);
        trainerReportRepository.delete(item);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    private void assertReportEditable(TrainerMonthlyReport monthlyReport) {
        if (monthlyReport.getStatus() == MonthlyReportStatus.PAID) {
            throw new UserException("Vyplacený výkaz nelze upravovat.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public TrainerMonthlyReportDto markReportPaid(Long reportId) {
        TrainerMonthlyReport monthlyReport = trainerMonthlyReportRepository.findById(reportId)
                .orElseThrow(() -> new UserException("Report with ID " + reportId + " not found.", HttpStatus.NOT_FOUND));

        monthlyReport.setStatus(MonthlyReportStatus.PAID);
        trainerMonthlyReportRepository.save(monthlyReport);

        LocalDate today = LocalDate.now();
        return toMonthlyReportDto(monthlyReport, today);
    }

    private TrainerMonthlyReport getEditableMonthlyReport(String email, Long reportId) {
        TrainerMonthlyReport monthlyReport = trainerMonthlyReportRepository.findById(reportId)
                .orElseThrow(() -> new UserException("Report with ID " + reportId + " not found.", HttpStatus.NOT_FOUND));

        if (monthlyReport.getTrainer().getEmail() == null
                || !monthlyReport.getTrainer().getEmail().equalsIgnoreCase(email)) {
            throw new UserException("Access denied.", HttpStatus.FORBIDDEN);
        }

        LocalDate today = LocalDate.now();
        boolean withinEditableWindow = isWithinEditableWindow(monthlyReport.getYear(), monthlyReport.getMonth(), today);
        if (!withinEditableWindow || monthlyReport.getStatus() != MonthlyReportStatus.PENDING) {
            throw new UserException("This report can no longer be edited.", HttpStatus.BAD_REQUEST);
        }

        return monthlyReport;
    }

    // A report stays editable for its own month, plus a grace period into the
    // following month (see REPORT_GRACE_PERIOD_DAYS) — but never for a month
    // that hasn't started yet.
    private boolean isWithinEditableWindow(int year, int month, LocalDate today) {
        YearMonth reportMonth = YearMonth.of(year, month);
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate graceDeadline = reportMonth.plusMonths(1).atDay(REPORT_GRACE_PERIOD_DAYS);
        return !reportMonth.isAfter(currentMonth) && !today.isAfter(graceDeadline);
    }

    private TrainerMonthlyReportDto toMonthlyReportDto(TrainerMonthlyReport mr, LocalDate today) {
        boolean withinEditableWindow = isWithinEditableWindow(mr.getYear(), mr.getMonth(), today);

        List<TrainerReportItemDto> items = mr.getReports().stream()
                .sorted(Comparator.comparing(TrainerReport::getStartDate)
                        .thenComparing(r -> r.getStartTime() != null ? r.getStartTime() : LocalTime.MIDNIGHT))
                .map(this::toItemDto)
                .toList();

        int totalMoney = items.stream().mapToInt(TrainerReportItemDto::money).sum();
        boolean editable = withinEditableWindow && mr.getStatus() == MonthlyReportStatus.PENDING;
        String approvedAt = mr.getApprovedAt() != null ? mr.getApprovedAt().format(REPORT_DATETIME_FORMATTER) : null;

        return new TrainerMonthlyReportDto(
                mr.getId(),
                mr.getYear(),
                mr.getMonth(),
                mr.getStatus().name(),
                mr.getNote(),
                approvedAt,
                editable,
                totalMoney,
                items
        );
    }

    private TrainerReportItemDto toItemDto(TrainerReport tr) {
        String formattedDate;
        if (tr.getEndDate() != null && !tr.getEndDate().equals(tr.getStartDate())) {
            formattedDate = tr.getStartDate().format(REPORT_DATE_FORMATTER) + " - " + tr.getEndDate().format(REPORT_DATE_FORMATTER);
        } else {
            formattedDate = tr.getStartDate().format(REPORT_DATE_FORMATTER);
        }

        boolean manual = tr.getSourceId() == null;
        boolean trainingAttendance = !manual && tr.getReportCategory() == ReportCategory.TRAINING;

        return new TrainerReportItemDto(
                tr.getId(),
                formattedDate,
                tr.getName(),
                tr.getDayOfWeek(),
                tr.getStartTime(),
                tr.getEndTime(),
                tr.getReportCategory() != null ? tr.getReportCategory().getName() : null,
                tr.getMoney(),
                manual,
                trainingAttendance,
                tr.getSourceId()
        );
    }
}
