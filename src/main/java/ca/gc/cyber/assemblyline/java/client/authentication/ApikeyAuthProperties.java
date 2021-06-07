package ca.gc.cyber.assemblyline.java.client.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "assemblyline-java-client.api-auth")
public class ApikeyAuthProperties {

    String apikey;
    String username;
}
