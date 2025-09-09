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

@Table(name = "training_unit")
@Entity
@NoArgsConstructor
@Setter
@Getter
public class TrainingUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    @OneToMany(mappedBy = "trainingUnit", orphanRemoval = true)
    private List<TrainerAttendance> trainerAttendances = new ArrayList<>();

    @OneToMany(mappedBy = "trainingUnit", orphanRemoval = true)
    private List<ChildTrainingAttendance> childTrainingAttendances = new ArrayList<>();

    private LocalDate date;

    private String description;

    private boolean current;

    public boolean addTrainerAttendance(TrainerAttendance trainerAttendance) {
        if (!this.trainerAttendances.contains(trainerAttendance)) {
            this.trainerAttendances.add(trainerAttendance);
            return true;
        }
        return false;
    }

    public boolean addChildAttendance(ChildTrainingAttendance childTrainingAttendance) {
        if (this.childTrainingAttendances.contains(childTrainingAttendance)) {
            this.childTrainingAttendances.remove(childTrainingAttendance);
            return true;
        }
        return false;
    }
}
