package pg.masters.backend.recognition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import pg.masters.backend.control.dto.RecognitionConfigurationDto;
import pg.masters.backend.recognition.azure.AzureService;
import pg.masters.backend.recognition.browser.BrowserService;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.recognition.google.GoogleService;
import pg.masters.backend.recognition.revai.RevaiService;

/**
 * This class manages selected Recognition provider, later this config need to be stored per user
 * constructor sets base service
 */
@Log4j2
@Service
public class RecognitionServiceManager implements ApplicationListener<RecognitionEvent> {
    private final GoogleService googleService;
    private final AzureService azureService;
    private final BrowserService browserService;
    private final RevaiService revaiService;
    @Getter
    @Setter(value = AccessLevel.PRIVATE)
    private RecognitionService selectedRecognitionService;

    public RecognitionServiceManager(GoogleService googleService, AzureService azureService,
                                     BrowserService browserService, RevaiService revaiService) {
        this.googleService = googleService;
        this.azureService = azureService;
        this.browserService = browserService;
        this.selectedRecognitionService = googleService;
        this.revaiService = revaiService;
    }

    /**
     * Important: change should make sure that current recognition is stopped
     *
     * @param recognitionServiceProvider provides selected enum value of service provider
     */
    public void changeRecognitionService(RecognitionServiceProvider recognitionServiceProvider) {
        if (recognitionServiceProvider == selectedRecognitionService.getRecognitionServiceProvider()) {
            log.warn("Selected service already in use!");
            return;
        }

        log.info("Trying to change recognition provider to: " + recognitionServiceProvider.getName());
        switch (recognitionServiceProvider) {
            case AZURE -> setSelectedRecognitionService(this.azureService);
            case GOOGLE -> setSelectedRecognitionService(this.googleService);
            case REV_AI -> setSelectedRecognitionService(this.revaiService);
            case BROWSER -> setSelectedRecognitionService(this.browserService);
            default -> log.warn("Recognition service couldn't be set");
        }
    }

    public RecognitionConfigurationDto map(RecognitionServiceProvider recognitionServiceProvider) {
        return switch (recognitionServiceProvider) {
            case AZURE, GOOGLE, REV_AI, SPEECH_TEXT_AI ->  new RecognitionConfigurationDto(recognitionServiceProvider);
            case BROWSER -> new RecognitionConfigurationDto(recognitionServiceProvider.getName(),
                    recognitionServiceProvider.getAudioMimeType().getMimeTypeOption(), 0,
                    recognitionServiceProvider.getReplyType());
        };
    }

    public RecognitionConfigurationDto getCurrentRecognitionServiceProvider() {
        return map(selectedRecognitionService.getRecognitionServiceProvider());
    }

    @Override
    public void onApplicationEvent(RecognitionEvent event) {
        selectedRecognitionService.handleNewRecognition(event.getText());
    }
}
