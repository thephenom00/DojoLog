package cz.cvut.fel.attendance.service.repository;

import cz.cvut.fel.attendance.service.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
}
