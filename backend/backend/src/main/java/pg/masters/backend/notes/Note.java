package pg.masters.backend.notes;

import lombok.Getter;
import lombok.Setter;
import pg.masters.backend.auth.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_generator")
    @SequenceGenerator(name = "note_generator", sequenceName = "note_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 32)
    private String title;

    @Lob
    private String text = "";

    @Column(name = "created_on")
    private LocalDate createdOn;

    @Column(name = "last_modification")
    private LocalDateTime lastModification;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
