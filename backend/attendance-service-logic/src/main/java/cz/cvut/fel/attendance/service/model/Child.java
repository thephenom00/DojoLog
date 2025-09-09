package cz.cvut.fel.attendance.service.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "child")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "child")
    private List<ChildTrainingAttendance> trainingAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "child")
    private List<ChildEventAttendance> eventAttendances = new ArrayList<>();

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String street;

    private String city;

    private int zip;

    private String birthNumber;

    private Long requestedTrainingId;

    public Child(String firstName, String lastName, LocalDate dateOfBirth, String street, String city, int zip, String birthNumber, Long requestedTrainingId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.birthNumber = birthNumber;
        this.requestedTrainingId = requestedTrainingId;
    }

    public boolean addChildAttendance(ChildTrainingAttendance childTrainingAttendance) {
        if (!this.trainingAttendances.contains(childTrainingAttendance)) {
            this.trainingAttendances.add(childTrainingAttendance);
            return true;
        }
        return false;
    }

}
