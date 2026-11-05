package cz.cvut.fel.attendance.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
public class AppSettings {

    @Id
    private Long id = 1L;

    private boolean enrollmentOpen;
    private boolean trainingGeneration;
}
