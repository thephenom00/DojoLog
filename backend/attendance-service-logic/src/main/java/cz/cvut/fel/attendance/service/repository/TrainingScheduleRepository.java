package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainingSchedule;
import cz.cvut.fel.attendance.service.model.TrainingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingScheduleRepository extends JpaRepository<TrainingSchedule, Long> {
    @Query("""
        SELECT tu FROM TrainingUnit tu
        WHERE tu.trainingSchedule.id = :id AND tu.current = true
    """)
    TrainingUnit findCurrentTrainingUnitById(@Param("id") Long id);

    @Query("""
        SELECT ts FROM TrainingSchedule ts
        WHERE ts.training.id = :trainingId
    """)
    List<TrainingSchedule> findByTrainingId(@Param("trainingId") Long trainingId);
}
