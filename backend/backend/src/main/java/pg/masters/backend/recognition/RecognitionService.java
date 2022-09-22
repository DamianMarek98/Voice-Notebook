package pg.masters.backend.recognition;

import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface RecognitionService {
    /**
     * add new data form file to stream recognition
     * @param filePath - path to file to recognize from
     */
    void recognizeInStream(String filePath);

    /**
     * @return recognized text by streaming recognition service
     */
    Optional<String> getReply();

    /**
     * @param text new reply text form recognition service
     */
    void handleNewRecognition(String text);

    /**
     * shoud stop recognition service and clear all resources
     */
    void stopStreamRecognition();

    /**
     * @return value of RecognitionServiceProvider enum assigned to service
     */
    RecognitionServiceProvider getRecognitionServiceProvider();

    default Optional<String> wrapReply(@Nonnull String reply) {
        return reply.equals("") ? Optional.empty() : Optional.of(reply);
    }
}
