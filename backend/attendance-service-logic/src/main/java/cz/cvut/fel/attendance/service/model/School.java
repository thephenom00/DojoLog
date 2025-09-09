package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Table(name = "school")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "school", orphanRemoval = true)
    private List<Training> trainings = new ArrayList<>();

    @OneToMany(mappedBy = "school", orphanRemoval = true)
    private List<Holiday> holidays = new ArrayList<>();

    private String name;

    private String address;

    private String city;

    private String contactPerson;

    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    public School(String name, String address, String city, String contactPerson, String contactNumber, String instructions) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.instructions = instructions;
    }

    public void addTraining(Training training) {
        this.trainings.add(training);
    }

}
