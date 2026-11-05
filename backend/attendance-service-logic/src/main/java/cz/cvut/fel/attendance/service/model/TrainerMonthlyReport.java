package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.MonthlyReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "trainer_monthly_report",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "year", "month"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    private int year;

    private int month;

    @Enumerated(EnumType.STRING)
    private MonthlyReportStatus status = MonthlyReportStatus.PENDING;

    private String note;

    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "trainerMonthlyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TrainerReport> reports = new ArrayList<>();

    public TrainerMonthlyReport(Trainer trainer, int year, int month) {
        this.trainer = trainer;
        this.year = year;
        this.month = month;
        this.status = MonthlyReportStatus.PENDING;
    }
}
