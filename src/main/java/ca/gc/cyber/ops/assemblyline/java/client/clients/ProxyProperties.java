package ca.gc.cyber.ops.assemblyline.java.client.clients;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "assemblyline-java-client.proxy")
public class ProxyProperties {

    /**
     * Proxy host.
     */
    private String host;

    /**
     * Proxy port.
     */
    @Min(1)
    private int port;
}
