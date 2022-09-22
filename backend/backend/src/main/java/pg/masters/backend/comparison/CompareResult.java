package pg.masters.backend.comparison;

import lombok.Getter;
import lombok.Setter;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CompareResult {

    @Id
    @GeneratedValue
    private Long id;

    private Double wordErrorRate;

    private Double levenshteinDistance;

    private Long userId;

    private Long textId;

    private String transcriptGroupName;

    @Enumerated(value = EnumType.STRING)
    private RecognitionServiceProvider provider;

    @Transient
    private String text = "";
}
