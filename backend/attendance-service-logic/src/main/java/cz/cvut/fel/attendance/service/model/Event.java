package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private List<ChildEventAttendance> childEventAttendances = new ArrayList<>();

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private String location;

    private LocalTime startTime;

    private LocalTime endTime;

    private int places;

    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int trainerSalary;

    private String eventType;

    public Event(String name, LocalDate startDate, LocalDate endDate, String location, LocalTime startTime, LocalTime endTime, int places, int price, String description) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.places = places;
        this.price = price;
        this.description = description;
    }

}
