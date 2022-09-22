package pg.masters.backend.transcript;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.masters.backend.transcript.dto.TranscriptGroupDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TranscriptGroupRepository extends JpaRepository<TranscriptGroup, String> {

    @Query("""
            select
                new pg.masters.backend.transcript.dto.TranscriptGroupDto(
                    tg.name,
                    tg.finished,
                    tg.createdOn,
                    tg.status,
                    tg.errorMessage
                )
            from TranscriptGroup tg
            where tg.user.id = :id
            """)
    List<TranscriptGroupDto> findAllByUserId(@Param("id") Long userId);

    List<TranscriptGroup> findAllByCreatedOnAfter(LocalDateTime after);
}
