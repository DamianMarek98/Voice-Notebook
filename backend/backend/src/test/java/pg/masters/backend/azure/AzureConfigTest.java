package pg.masters.backend.azure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pg.masters.backend.BackendApplication;
import pg.masters.backend.recognition.azure.AzureProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = BackendApplication.class)
public class AzureConfigTest {

    @Autowired
    private AzureProperties azureProperties;

    @Test
    public void whenPropertiesLoadedViaJsonPropertySource_thenLoadFlatValues() {
        assertEquals("germanywestcentral", azureProperties.getRegion());
    }
}
