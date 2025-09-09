package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByName(String name);

}
