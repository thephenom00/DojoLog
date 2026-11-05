package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Child;
import cz.cvut.fel.attendance.service.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    boolean existsByBirthNumber(String birthNumber);
    boolean existsByFirstNameAndLastNameAndParentAndActiveTrue(String firstName, String lastName, Parent parent);
    List<Child> findByRequestedTrainingIdIsNotNull();
    List<Child> findByActiveTrue();

    @Modifying
    @Query("UPDATE Child c SET c.training = null, c.paid = false")
    int clearTrainings();
}
