package ca.gc.cyber.ops.assemblyline.java.client.clients;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "assemblyline-java-client")
public class AssemblylineClientProperties {

    private String url;

}
