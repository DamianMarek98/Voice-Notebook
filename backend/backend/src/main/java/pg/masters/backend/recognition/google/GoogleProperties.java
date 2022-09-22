package pg.masters.backend.recognition.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@PropertySource(value = "classpath:application.properties")
@ConfigurationProperties(prefix = "google")
public class GoogleProperties {
    @Value("${google.key-path}")
    private String keyPath;
}
