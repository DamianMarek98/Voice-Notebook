package pg.masters.backend.transcript;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pg.masters.backend.auth.AuthService;
import pg.masters.backend.auth.exceptions.NotAuthenticatedException;
import pg.masters.backend.transcript.dto.RealTextDto;
import pg.masters.backend.transcript.dto.TranscriptDto;
import pg.masters.backend.transcript.dto.TranscriptGroupDto;
import pg.masters.backend.transcript.errors.TranscribeGroupNameNotUniqueException;
import pg.masters.backend.transcript.errors.TranscriptGroupNotFoundException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static pg.masters.backend.recognition.enums.RecognitionServiceProvider.SPEECH_TEXT_AI;

@Log4j2
@RestController
@RequestMapping("/transcript")
@RequiredArgsConstructor
public class TranscriptController {

    private final AuthService authService;
    private final TranscriptService transcriptService;


    @GetMapping("/{name}/all")
    public List<TranscriptDto> getAllGroupTranscripts(@PathVariable("name") String name) {
        return transcriptService.getAllTranscriptsFromGroup(name);
    }

    @GetMapping(value = "{name}/real-text")
    public RealTextDto getRealText(@PathVariable("name") String name) throws TranscriptGroupNotFoundException {
        return new RealTextDto(transcriptService.findById(name).getRealText());
    }

    @PutMapping("{name}/update-real-text")
    public void updateRealText(@PathVariable("name") String name, @RequestBody String text)
            throws TranscriptGroupNotFoundException {
        transcriptService.updateTranscriptGroupRealText(name, text);
    }

    @GetMapping("/groups")
    public List<TranscriptGroupDto> getUsersTranscriptGroupViews() throws NotAuthenticatedException {
        var user = authService.getCurrentContextUser();
        return transcriptService.getAllUsersTranscriptGroups(user.getId());
    }

    @PostMapping("/new/{name}")
    public void createTranscriptGroup(@PathVariable("name") String name, @RequestParam("file")MultipartFile file)
            throws NotAuthenticatedException, TranscribeGroupNameNotUniqueException {
        var user = authService.getCurrentContextUser();
        try {
            var transcribeGroup = transcriptService.createTranscriptGroup(name, file.getBytes(), user);
            //executed asynchronously
            var thread = new Thread(() -> {
                transcriptService.updateTranscriptGroupStatus(transcribeGroup, Status.IN_PROGRESS);
                transcriptService.transcribeGroup(transcribeGroup, user);
            });
            thread.start();
        } catch (IOException e) {
            log.error("Couldn't read bytes from file, tried to create new group with name: " + name);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't read bytes from file");
        }
    }

    @PostMapping("/add-stai/")
    public void addStai(@RequestBody AddStaiRes addStaiRes) {
        var transcriptGroup = transcriptService.getById(addStaiRes.name).orElseThrow(EntityNotFoundException::new);
        var transcript = new Transcript();
        transcript.setSuccessful(true);
        transcript.setResultText(addStaiRes.text);
        transcript.setCreatedOn(LocalDateTime.now());
        transcript.setTranscriptGroup(transcriptGroup);
        transcript.setProvider(SPEECH_TEXT_AI);
        transcriptService.saveTranscript(transcript);
    }

    public record AddStaiRes(String name, String text) {

    }
}
