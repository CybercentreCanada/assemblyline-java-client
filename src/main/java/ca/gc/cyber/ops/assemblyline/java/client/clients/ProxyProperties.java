package ca.gc.cyber.ops.assemblyline.java.client.clients;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private String port;
}
