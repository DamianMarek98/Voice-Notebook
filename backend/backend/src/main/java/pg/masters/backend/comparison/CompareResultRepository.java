package pg.masters.backend.comparison;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompareResultRepository extends JpaRepository<CompareResult, Long> {
}
