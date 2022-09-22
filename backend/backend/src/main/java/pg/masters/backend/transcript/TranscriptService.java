package pg.masters.backend.transcript;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import pg.masters.backend.auth.User;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;
import pg.masters.backend.transcript.dto.TranscriptDto;
import pg.masters.backend.transcript.dto.TranscriptGroupDto;
import pg.masters.backend.transcript.errors.TranscribeFileException;
import pg.masters.backend.transcript.errors.TranscribeGroupNameNotUniqueException;
import pg.masters.backend.transcript.errors.TranscriptGroupNotFoundException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;
    private final TranscriptGroupRepository transcriptGroupRepository;
    private final TranscribeService transcribeService;
    private final PlatformTransactionManager platformTransactionManager;
    private TransactionTemplate transactionTemplate;

    private final TranscriptStatusNotifier transcriptStatusNotifier;

    @PostConstruct
    private void initTransactionTemplate() {
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    }


    public List<TranscriptGroupDto> getAllUsersTranscriptGroups(Long userId) {
        return transcriptGroupRepository.findAllByUserId(userId);
    }

    public List<TranscriptDto> getAllTranscriptsFromGroup(String groupName) {
        return transcriptRepository.findAllByTranscriptGroupName(groupName);
    }

    public TranscriptGroup findById(String name) throws TranscriptGroupNotFoundException {
        return transcriptGroupRepository.findById(name).orElseThrow(() -> new TranscriptGroupNotFoundException(name));
    }

    public void updateTranscriptGroupRealText(String name, String text) throws TranscriptGroupNotFoundException {
        var transcriptGroup = transcriptGroupRepository.findById(name)
                .orElseThrow(() -> new TranscriptGroupNotFoundException(name));
        transcriptGroup.setRealText(text);
        transcriptGroupRepository.save(transcriptGroup);
    }

    public TranscriptGroup createTranscriptGroup(String name, byte[] file, User user)
            throws TranscribeGroupNameNotUniqueException {
        if (transcriptGroupRepository.existsById(name)) {
            throw new TranscribeGroupNameNotUniqueException(name);
        }

        var transcriptGroup = new TranscriptGroup();
        transcriptGroup.setName(name);
        transcriptGroup.setCreatedOn(LocalDateTime.now());
        transcriptGroup.setWaveFile(file);
        transcriptGroup.setUser(user);
        return transcriptGroupRepository.saveAndFlush(transcriptGroup);
    }

    @Transactional
    public void updateTranscriptGroupStatus(TranscriptGroup tg, Status status) {
        tg.setStatus(status);
        transcriptGroupRepository.saveAndFlush(tg);
    }

    public void transcribeGroup(@NotNull TranscriptGroup transcriptGroup, User user) {
        try {
            log.info("Starting transcriptions for group: " + transcriptGroup.getName());
            var results = transcribeService.transcribe(transcriptGroup);
            log.info("Finished transcriptions for group: " + transcriptGroup.getName());
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    for (var result : results) {
                        var resultText = result.transcribeStatus().equals(TranscribeService.TranscribeStatus.SUCCESS) ?
                                result.result() : "";
                        Optional<String> errorMessage = result.transcribeStatus().equals(TranscribeService.TranscribeStatus.SUCCESS) ?
                                Optional.empty() : Optional.of(result.result());
                        saveTranscript(transcriptGroup, resultText, errorMessage, result.provider());
                    }
                }
            });
        } catch (TranscribeFileException e) {
            log.error(e.getMessage());
            transcriptGroup.setErrorMessage(e.getMessage());
        } finally {
            transcriptGroup.setStatus(Status.FINISHED);
            transcriptGroupRepository.save(transcriptGroup);
            transcriptStatusNotifier.sendStatus(transcriptGroup, user);
        }
    }

    private void saveTranscript(TranscriptGroup transcriptGroup, String resultText, Optional<String> errorMessage,
                                     RecognitionServiceProvider recognitionServiceProvider) {
        var transcript = new Transcript();
        transcript.setCreatedOn(LocalDateTime.now());
        transcript.setResultText(resultText);
        errorMessage.ifPresent(errMsg -> {
            transcript.setErrorMessage(errMsg);
            transcript.setSuccessful(false);
        });
        transcript.setProvider(recognitionServiceProvider);
        Hibernate.initialize(transcriptGroup.getTranscripts());
        transcriptGroup.addTranscript(transcript);
        transcriptRepository.save(transcript);
    }

    Optional<TranscriptGroup> getById(String id) {
        return transcriptGroupRepository.findById(id);
    }

    void saveTranscript(Transcript transcript) {
        transcriptRepository.save(transcript);
    }
}
