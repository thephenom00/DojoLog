package cz.cvut.fel.attendance.service.model;

import cz.cvut.fel.attendance.service.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("TRAINER")
public class Trainer extends User {
    @ManyToMany(mappedBy = "trainers")
    private List<Training> trainings = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")
    private List<TrainerAttendance> trainingAttendances = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "trainers_in_events",
            joinColumns = @JoinColumn(name = "trainer_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private List<Event> events = new ArrayList<>();

    private int hourlyRate;

    public Trainer(String firstName, String lastName, String email, String phoneNumber, String password, int hourlyRate) {
        super(firstName, lastName, email, phoneNumber, password, Role.ROLE_TRAINER);
        this.hourlyRate = hourlyRate;
    }

    public boolean addTraining(Training training) {
        if (!this.trainings.contains(training)) {
            this.trainings.add(training);
            return true;
        }
        return false;
    }

    public boolean removeTraining(Training training) {
        if (this.trainings.contains(training)) {
            this.trainings.remove(training);
            return true;
        }
        return false;
    }

    public boolean addTrainerAttendance(TrainerAttendance trainerAttendance) {
        if (!this.trainingAttendances.contains(trainerAttendance)) {
            this.trainingAttendances.add(trainerAttendance);
            return true;
        }
        return false;
    }




}
