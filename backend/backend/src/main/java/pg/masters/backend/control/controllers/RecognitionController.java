package pg.masters.backend.control.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pg.masters.backend.recognition.RecognitionServiceManager;

@RestController
@RequestMapping("/recognition")
@RequiredArgsConstructor
public class RecognitionController {

    private final RecognitionServiceManager recognitionServiceManager;

    @PostMapping("/stop")
    ResponseEntity<String> stopRecognition() {
        this.recognitionServiceManager.getSelectedRecognitionService().stopStreamRecognition();
        return ResponseEntity.ok().body("Recognition Stopped!");
    }
}
