package cz.cvut.fel.attendance.service.model;


import jakarta.persistence.CascadeType;
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

    @OneToMany(mappedBy = "child", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private List<ChildTrainingAttendance> trainingAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "child", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private List<ChildEventAttendance> eventAttendances = new ArrayList<>();

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String street;

    private String city;

    private int zip;

    private String birthNumber;

    private Boolean paid = false;

    private Long requestedTrainingId;

    private boolean active = true;

    public Child(String firstName, String lastName, LocalDate dateOfBirth, String street, String city, int zip, String birthNumber, Long requestedTrainingId) {
        this.firstName = firstName != null ? firstName.trim() : null;
        this.lastName = lastName != null ? lastName.trim() : null;
        this.dateOfBirth = dateOfBirth;
        this.street = street != null ? street.trim() : null;
        this.city = city != null ? city.trim() : null;
        this.zip = zip;
        this.birthNumber = birthNumber != null ? birthNumber.trim() : null;
        this.requestedTrainingId = requestedTrainingId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public void setStreet(String street) {
        this.street = street != null ? street.trim() : null;
    }

    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
    }

    public void setBirthNumber(String birthNumber) {
        this.birthNumber = birthNumber != null ? birthNumber.trim() : null;
    }

    public boolean addChildAttendance(ChildTrainingAttendance childTrainingAttendance) {
        if (!this.trainingAttendances.contains(childTrainingAttendance)) {
            this.trainingAttendances.add(childTrainingAttendance);
            return true;
        }
        return false;
    }

}
