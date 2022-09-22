package pg.masters.backend.recognition.revai;

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
@ConfigurationProperties(prefix = "revai")
public class RevaiProperties {
    @Value("${revai.token}")
    private String token;
}
