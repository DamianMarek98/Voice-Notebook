package pg.masters.backend.commands;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.dialogflow.v2.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Log4j2
@Service
public class CommandService {

    private static final String LANGUAGE_CODE = "pl";
    private static final String SESSION_ID = "test";
    private static final String PROJECT_ID = "voice-web-notebook";

    public CommandDto detectIntentTexts(String text) throws IOException, ApiException {
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(PROJECT_ID, SESSION_ID);

            // Set the text (hello) and language code (en-US) for the query
            TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(LANGUAGE_CODE);
            // Build the query with the TextInput
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
            // Performs the detect name request
            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            // Display the query result
            QueryResult queryResult = response.getQueryResult();
            var command = map(queryResult.getIntent().getDisplayName());
            if (command == Command.CHANGE_PROVIDER) {
                return new CommandDto(command.name(), getStringValue(queryResult, "provider"));
            }

            return new CommandDto(command.name());
        } catch (CommandNotFoundException e) {
            log.error(e.getMessage());
        }

        return new CommandDto(Command.ERROR.name());
    }

    public Command map(String intent) throws CommandNotFoundException {
        return Arrays.stream(Command.values())
                .filter(c -> c.getIntent().equals(intent))
                .findFirst()
                .orElseThrow(() -> new CommandNotFoundException(intent));
    }

    public String getStringValue(QueryResult queryResult, String key) {
        for (var entry : queryResult.getParameters().getFieldsMap().entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue().getStringValue();
            }
        }

        return "";
    }
}
