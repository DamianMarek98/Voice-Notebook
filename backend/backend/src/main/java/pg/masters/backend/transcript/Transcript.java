package pg.masters.backend.transcript;

import lombok.Getter;
import lombok.Setter;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Transcript {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transcript_generator")
    @SequenceGenerator(name = "transcript_generator", sequenceName = "transcript_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private RecognitionServiceProvider provider;

    @Lob
    private String resultText = "";

    private boolean successful = true;

    private String errorMessage = "";

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    private TranscriptGroup transcriptGroup;
}
