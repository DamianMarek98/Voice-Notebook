package pg.masters.backend.control.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import pg.masters.backend.recognition.RecognitionServiceManager;
import pg.masters.backend.utils.ResourceUtils;

import java.io.FileOutputStream;
import java.util.Base64;

@Log4j2
@Controller
@RequiredArgsConstructor
public class SpeechController {

    private static final String TEMP_PATH = ResourceUtils.audioTempFilePath();
    private final RecognitionServiceManager recognitionServiceManager;

    @MessageMapping("/speech")
    @SendToUser("/topic/text")
    public String speechToText(String base64Audio) {
        var decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(base64Audio.split(",")[1]);
        var filePath = TEMP_PATH + "test.webm";
        try (var fos = new FileOutputStream(filePath)) {
            fos.write(decodedBytes);
        } catch (Exception e) {
            log.error("Unable to write to file");
        }

        try {
            recognitionServiceManager.getSelectedRecognitionService().recognizeInStream(filePath);
        } catch (Exception e) {
            log.warn("Speech recognition from file: unsuccessful");
        }

        return recognitionServiceManager.getSelectedRecognitionService().getReply().orElse("");
    }
}
