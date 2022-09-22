package pg.masters.backend.transcript;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pg.masters.backend.auth.User;

@Controller
@RequiredArgsConstructor
public class TranscriptStatusNotifier {

    private final SimpMessagingTemplate template;

    public void sendStatus(TranscriptGroup transcriptGroup, User user) {
        template.convertAndSend("/topic/transcript-status/" + user.getUsername(),
                "Transkrypcja grupy: " + transcriptGroup.getName() + " zakończona!");
    }
}
