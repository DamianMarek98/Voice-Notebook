package pg.masters.backend.recognition.revai;

import ai.rev.speechtotext.ApiClient;
import ai.rev.speechtotext.models.asynchronous.RevAiJobOptions;
import ai.rev.speechtotext.models.streaming.SessionConfig;
import ai.rev.speechtotext.models.streaming.StreamContentType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.RecognitionService;
import pg.masters.backend.recognition.azure.AzureService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.utils.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
public class RevaiService implements RecognitionService {

    private final RevaiProperties revaiProperties;
    private final RevaiStreamRecognition revaiStreamRecognition;
    private final StreamContentType streamContentType;

    public RevaiService(RevaiProperties revaiProperties, RevaiStreamRecognition revaiStreamRecognition) {
        this.revaiProperties = revaiProperties;
        this.revaiStreamRecognition = revaiStreamRecognition;
        this.streamContentType = configureStreamContentType();
    }

    private StreamContentType configureStreamContentType() {
        // Configure the streaming content type
        var sct = new StreamContentType();
        sct.setContentType("audio/x-wav"); // audio content type
        // fixme currently does not support language set ...
        return sct;
    }

    @Override
    public void recognizeInStream(String filePath) {
        if (!revaiStreamRecognition.started()) {
            this.revaiStreamRecognition.start(new RevaiConfig(streamContentType, prepareSessionConfig(),
                    revaiProperties.getToken()));
        }

        revaiStreamRecognition.pushFile(filePath);
    }

    private SessionConfig prepareSessionConfig() {
        var sessionConfig = new SessionConfig();
        sessionConfig.setMetaData("Streaming from the Java SDK");
        sessionConfig.setFilterProfanity(true);
        return sessionConfig;
    }

    @Override
    public Optional<String> getReply() {
        return Optional.empty();
    }

    @Override
    public void handleNewRecognition(String text) {
        log.info(text);
    }

    @Override
    public void stopStreamRecognition() {
        this.revaiStreamRecognition.stop();
    }

    @Override
    public RecognitionServiceProvider getRecognitionServiceProvider() {
        return RecognitionServiceProvider.REV_AI;
    }

    public String transcriptionFromFile(String filePath) throws IOException, InterruptedException, RevaiTranscriptionException {
        var apiClient = new ApiClient(revaiProperties.getToken());
        var options = new RevAiJobOptions();
        options.setLanguage("pl");
        var revAiJob = apiClient.submitJobLocalFile(filePath, options);
        var newlyRefreshedRevAiJob = apiClient.getJobDetails(revAiJob.getJobId());

        while (!newlyRefreshedRevAiJob.getJobStatus().getStatus().equals("transcribed") && !newlyRefreshedRevAiJob.getJobStatus().getStatus().equals("failed")) {
            Thread.sleep(5000);
            newlyRefreshedRevAiJob = apiClient.getJobDetails(revAiJob.getJobId());
        }

        if (newlyRefreshedRevAiJob.getJobStatus().getStatus().equals("failed")) {
            log.error("RevAi job with id: " + revAiJob.getJobId() + " + failed!");
            throw new RevaiTranscriptionException(newlyRefreshedRevAiJob.getJobStatus().toString());
        }


        final String transcriptText = apiClient.getTranscriptText(revAiJob.getJobId());
        return transcriptText.substring(21); // to remove Speaker 0 00:00:00:0X
    }
}
