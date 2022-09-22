package pg.masters.backend.recognition.google;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pg.masters.backend.recognition.google.errors.KeyNotSetException;
import pg.masters.backend.recognition.google.errors.CredentialsException;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCredentialsService {

    private final GoogleProperties googleProperties;
    private GoogleCredentials googleCredentials = null;

    public GoogleCredentials getGoogleCredentials() {
        checkCredentials();
        return googleCredentials;
    }

    private void checkCredentials() {
        if (googleCredentials == null) {
            if (this.googleProperties.getKeyPath() == null) {
                throw new KeyNotSetException("DialogFlow auth key");
            }

            try {
                googleCredentials = GoogleCredentials.fromStream(
                        new FileInputStream(googleProperties.getKeyPath()));
            } catch (IOException e) {
                log.error("Unable to read gcp auth credentials: " + e);
                throw new CredentialsException("Google cloud platform");
            }
        }
    }
}
