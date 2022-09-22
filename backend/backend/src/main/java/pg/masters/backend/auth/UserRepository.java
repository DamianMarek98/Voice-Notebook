package pg.masters.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findUserByUsername(String username);

    @Query("select u from app_user u " +
            "left join fetch u.notes n " +
            "where u.id = :userId ")
    Optional<User> findByIdWithNotesFetched(Long userId);
}
