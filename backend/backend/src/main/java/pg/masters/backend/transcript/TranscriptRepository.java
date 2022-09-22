package pg.masters.backend.transcript;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pg.masters.backend.transcript.dto.TranscriptDto;

import java.util.List;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {

    @Query("""
            SELECT
            new pg.masters.backend.transcript.dto.TranscriptDto(
                t.id,
                t.provider,
                t.resultText,
                t.successful,
                t.errorMessage,
                t.createdOn
                )
            FROM Transcript t
            WHERE t.transcriptGroup.name = :name
            """)
    List<TranscriptDto> findAllByTranscriptGroupName(@Param("name") String name);

    @Query("""
            SELECT t
            FROM Transcript t
            WHERE t.transcriptGroup.name = :name
            """)
    List<Transcript> findAllByGroupName(@Param("name") String name);
}
