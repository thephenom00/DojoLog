package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerSubstitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long originalTrainerId;

    private Long substituteTrainerId;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "training_id")
    private Training training;

    public TrainerSubstitution(Long originalTrainerId, Long substituteTrainerId, LocalDate date, Training training) {
        this.originalTrainerId = originalTrainerId;
        this.substituteTrainerId = substituteTrainerId;
        this.date = date;
        this.training = training;
    }
}
