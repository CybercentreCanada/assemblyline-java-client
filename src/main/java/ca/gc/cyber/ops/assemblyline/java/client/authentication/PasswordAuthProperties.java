package ca.gc.cyber.ops.assemblyline.java.client.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "assemblyline-java-client.password-auth")
public class PasswordAuthProperties {

    String password;
    String username;
}
