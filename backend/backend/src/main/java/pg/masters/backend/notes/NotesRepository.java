package pg.masters.backend.notes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotesRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n " +
            "where n.user.id = :userId " +
            "order by n.lastModification desc")
    List<Note> getUserNotes(Long userId);
}
