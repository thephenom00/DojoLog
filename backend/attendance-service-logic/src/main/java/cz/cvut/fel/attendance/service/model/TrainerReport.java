package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.ReportCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "trainer_report")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_monthly_report_id")
    private TrainerMonthlyReport trainerMonthlyReport;

    private LocalDate startDate;

    private LocalDate endDate;

    private String name;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    private ReportCategory reportCategory;

    private Long sourceId;

    private LocalTime startTime;

    private LocalTime endTime;

    private int money;

    public TrainerReport(LocalDate startDate, LocalDate endDate, String name, DayOfWeek dayOfWeek, ReportCategory reportCategory, Long sourceId, LocalTime startTime, LocalTime endTime, int money) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name != null ? name.trim() : null;
        this.dayOfWeek = dayOfWeek;
        this.reportCategory = reportCategory;
        this.sourceId = sourceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.money = money;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}
