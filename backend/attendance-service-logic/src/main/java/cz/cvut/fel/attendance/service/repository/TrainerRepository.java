package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    List<Trainer> findByActiveTrue();
}
