package pg.masters.backend.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;
    private final ObjectMapper mapper = new ObjectMapper();

    @MessageMapping("/possible-command")
    @SendToUser("/topic/command")
    public String handleCommand(String transcription) {
        log.info("Got transcription: " + transcription);
        CommandDto command;
        try {
            command = commandService.detectIntentTexts(transcription);
        } catch (Exception e) {
            log.warn(e.getMessage());
            command = new CommandDto(Command.ERROR.name());
        }

        log.info("Mapped to: " + command.name());
        try {
            return mapper.writeValueAsString(command);
        } catch (JsonProcessingException e) {
            log.error("Couldn't serialize command to json " + command);
        }
        return "";
    }
}
