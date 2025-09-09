package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.TrainingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingUnitRepository extends JpaRepository<TrainingUnit, Long> {
    List<TrainingUnit> findByCurrentIsTrue();

    @Query("""
    SELECT tu FROM TrainingUnit tu
    JOIN tu.trainerAttendances ta
    JOIN ta.trainer tr
    WHERE tr.email = :email AND tu.current = true
    """)
    List<TrainingUnit> findUpcomingUnitsByTrainerEmail(@Param("email") String email);


    @Query("""
    SELECT tu FROM TrainingUnit tu
    JOIN tu.trainerAttendances ta
    JOIN ta.trainer tr
    WHERE tr.email = :email AND tu.current = false
    """)
    List<TrainingUnit> findPastUnitsByTrainerEmail(@Param("email") String email);


    @Query("""
    SELECT DISTINCT tu FROM TrainingUnit tu
    JOIN tu.training t
    JOIN t.children c
    JOIN c.parent p
    WHERE p.email = :email AND tu.current = true
    """)
    List<TrainingUnit> findUpcomingUnitsByParentEmail(@Param("email") String email);




}
