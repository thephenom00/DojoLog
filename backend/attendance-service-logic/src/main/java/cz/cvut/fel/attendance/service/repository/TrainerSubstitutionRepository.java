package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainerSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerSubstitutionRepository extends JpaRepository<TrainerSubstitution, Long> {
}
