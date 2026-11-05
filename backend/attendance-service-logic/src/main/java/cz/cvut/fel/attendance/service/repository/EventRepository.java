package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByName(String name);

    @Query("""
    SELECT e FROM Event e
    WHERE COALESCE(e.endDate, e.startDate) >= CURRENT_DATE
    ORDER BY e.startDate ASC, e.id ASC
    """)
    Page<Event> findUpcomingEvents(Pageable pageable);

    @Query("""
    SELECT e FROM Event e
    WHERE COALESCE(e.endDate, e.startDate) < CURRENT_DATE
    ORDER BY e.startDate DESC, e.id DESC
    """)
    Page<Event> findPastEvents(Pageable pageable);
}
