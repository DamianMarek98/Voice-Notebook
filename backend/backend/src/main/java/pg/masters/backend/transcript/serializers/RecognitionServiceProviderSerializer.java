package pg.masters.backend.transcript.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import pg.masters.backend.recognition.enums.RecognitionServiceProvider;

import java.io.IOException;

public class RecognitionServiceProviderSerializer extends JsonSerializer<RecognitionServiceProvider> {
    @Override
    public void serialize(RecognitionServiceProvider recognitionServiceProvider, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(recognitionServiceProvider.getName());
    }
}
