package pg.masters.backend.transcript;

import lombok.Getter;
import lombok.Setter;
import pg.masters.backend.auth.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class TranscriptGroup {

    @Id
    private String name;

    @Lob
    @Column(name = "wave_file")
    private byte[] waveFile;

    @Lob
    private String realText = "";

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private boolean finished = false;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    private String errorMessage;

    @Column(name = "transcribe_status")
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.NEW;

    @OneToMany(mappedBy = "transcriptGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transcript> transcripts = new ArrayList<>();

    public void addTranscript(Transcript transcript) {
        transcripts.add(transcript);
        transcript.setTranscriptGroup(this);
    }

    public void removeTranscript(Transcript transcript) {
        transcripts.remove(transcript);
        transcript.setTranscriptGroup(null);
    }
}
