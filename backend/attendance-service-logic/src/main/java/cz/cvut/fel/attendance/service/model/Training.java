package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Table(name = "training")
@Entity
@NoArgsConstructor
@Setter
@Getter
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "trainers_in_training",
            joinColumns = @JoinColumn(name = "training_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private List<Trainer> trainers = new ArrayList<>();

    @OneToMany(mappedBy = "training")
    private List<Child> children = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @OneToMany(mappedBy = "training", orphanRemoval = true, cascade = {CascadeType.REMOVE})
    private List<TrainingSchedule> trainingSchedules = new ArrayList<>();

    private String name;

    private int price;

    private int capacity;

    private int initialCapacity;

    private boolean active = true;

    public Training(String name, int price, int initialCapacity) {
        this.name = name != null ? name.trim() : null;
        this.price = price;
        this.initialCapacity = initialCapacity;
        this.capacity = initialCapacity;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public boolean addTrainer(Trainer trainer) {
        if (!this.trainers.contains(trainer)) {
            this.trainers.add(trainer);
            return true;
        }
        return false;
    }

    public boolean removeTrainer(Trainer trainer) {
        if (this.trainers.contains(trainer)) {
            this.trainers.remove(trainer);
            trainer.getTrainings().remove(this);
            return true;
        }
        return false;
    }

    public boolean addChild(Child child) {
        if (!this.children.contains(child)) {
            this.children.add(child);
            return true;
        }
        return false;
    }

    public boolean removeChild(Child child) {
        if (this.children.contains(child)) {
            this.children.remove(child);
            return true;
        }
        return false;
    }

}
