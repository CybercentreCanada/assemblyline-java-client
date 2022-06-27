package ca.gc.cyber.ops.assemblyline.java.client.clients;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@Data
@ConfigurationProperties(prefix = "assemblyline-java-client")
public class AssemblylineClientProperties {

    private String url;

    /**
     * Maximum size of data that can be buffered by the internal WebClient when reading a response from
     * AssemblyLine. The default value is 256 KiB, which is the same as Spring's default value.
     */
    private DataSize maxInMemorySize = DataSize.ofKilobytes(256);
}
