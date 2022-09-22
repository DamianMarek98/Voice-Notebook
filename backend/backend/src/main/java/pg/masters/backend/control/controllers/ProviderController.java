package pg.masters.backend.control.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pg.masters.backend.control.RecognitionProviderNotFoundException;
import pg.masters.backend.control.dto.RecognitionConfigurationDto;
import pg.masters.backend.recognition.RecognitionServiceManager;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final RecognitionServiceManager recognitionServiceManager;

    @PostMapping("/change/{name}")
    ResponseEntity<RecognitionConfigurationDto> changeRecognitionProvider(@PathVariable("name") String name)
            throws RecognitionProviderNotFoundException {
        var recognitionServiceProvider = Arrays.stream(RecognitionServiceProvider.values())
                .filter(rsp -> rsp.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RecognitionProviderNotFoundException(
                        "Recognition provide with name: " + name + " not found"));
        recognitionServiceManager.changeRecognitionService(recognitionServiceProvider);

        return ResponseEntity.ok(recognitionServiceManager.map(recognitionServiceProvider));
    }

    @GetMapping("/available")
    public ResponseEntity<List<String>> getAvailableProvider() {
        var recognitionServiceProviderNames = Arrays.stream(RecognitionServiceProvider.values())
                .map(RecognitionServiceProvider::getName)
                .toList();
        return ResponseEntity.ok(recognitionServiceProviderNames);
    }

    @GetMapping("/currently-in-use")
    ResponseEntity<RecognitionConfigurationDto> getCurrentlyInUseProvider() {
        return ResponseEntity.ok(recognitionServiceManager.getCurrentRecognitionServiceProvider());
    }
}
