package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT DISTINCT t FROM Training t WHERE t.school.id = :schoolId AND t.active = true")
    List<Training> findActiveBySchoolId(@Param("schoolId") Long schoolId);

    @Modifying
    @Query("UPDATE Training t SET t.capacity = t.initialCapacity")
    int setInitialCapacity();

}