package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.ReportCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "event")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(mappedBy = "events")
    private List<Trainer> trainers = new ArrayList<>();

    @OneToMany(mappedBy = "event", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private List<ChildEventAttendance> childEventAttendances = new ArrayList<>();

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate deadline;

    private String location;

    private LocalTime startTime;

    private LocalTime endTime;

    private int places;

    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int trainerSalary;

    @Enumerated(EnumType.STRING)
    private ReportCategory eventType;

    public Event(String name, LocalDate startDate, LocalDate endDate, String location, LocalTime startTime, LocalTime endTime, int places, int price, String description) {
        this.name = name != null ? name.trim() : null;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location != null ? location.trim() : null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.places = places;
        this.price = price;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim() : null;
    }
}
