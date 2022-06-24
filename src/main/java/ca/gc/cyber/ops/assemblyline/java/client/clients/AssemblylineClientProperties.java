package ca.gc.cyber.ops.assemblyline.java.client.clients;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "assemblyline-java-client")
public class AssemblylineClientProperties {

    private String url;

    /**
     * Maximum number of bytes that can be buffered by the internal WebClient when reading a response from
     * AssemblyLine. The default value is 256 KiB, which is the same as Spring's default value.
     */
    private int maxInMemorySize = 256 * 1024;
}
