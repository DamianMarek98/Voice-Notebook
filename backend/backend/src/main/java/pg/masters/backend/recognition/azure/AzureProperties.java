package pg.masters.backend.recognition.azure;

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
@ConfigurationProperties(prefix = "azure")
public class AzureProperties {
    @Value("${azure.key}")
    private String key;
    @Value("${azure.region}")
    private String region;
}
