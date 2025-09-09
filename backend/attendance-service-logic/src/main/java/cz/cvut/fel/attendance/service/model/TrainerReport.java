package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private Long trainerId;

    private Long trainerAttendanceId;

    private String date;

    private String name;

    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private double hours;

    private String type;

    private int money;

    public TrainerReport(Long trainerId, Long trainerAttendanceId, String date, String name, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, double hours, String type, int money) {
        this.trainerId = trainerId;
        this.trainerAttendanceId = trainerAttendanceId;
        this.date = date;
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hours = hours;
        this.type = type;
        this.money = money;
    }
}
