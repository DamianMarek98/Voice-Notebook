package pg.masters.backend.recognition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEventPublisher;

@Getter
@RequiredArgsConstructor
public abstract class StreamRecognition<T> implements StreamHandler<T> {

    protected final ApplicationEventPublisher applicationEventPublisher;

    @Accessors(fluent = true)
    protected boolean started = false;

    protected abstract void emitRecognition(String text);
}
