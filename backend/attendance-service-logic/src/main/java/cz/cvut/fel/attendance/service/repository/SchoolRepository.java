package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    boolean existsByName(String name);
}
