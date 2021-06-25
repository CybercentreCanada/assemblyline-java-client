package ca.gc.cyber.ops.assemblyline.java.client;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClient;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClientProperties;
import ca.gc.cyber.ops.assemblyline.java.client.clients.ProxyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(
        {ApikeyAuthProperties.class, PasswordAuthProperties.class, AssemblylineClientProperties.class,
                ProxyProperties.class})
public class AssemblylineClientConfig {

    @Bean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "apikey")
    public AssemblylineClient assemblylineClientApiAuth(ApikeyAuthProperties apikeyAuthProperties,
                                                        ObjectMapper defaultMapper,
                                                        AssemblylineClientProperties assemblylineClientProperties,
                                                        ProxyProperties proxyProperties) {
        ApikeyAuthentication apikeyAuthentication = new ApikeyAuthentication(apikeyAuthProperties);
        return new AssemblylineClient(assemblylineClientProperties, proxyProperties, defaultMapper,
                apikeyAuthentication);
    }

    @Bean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "password")
    public AssemblylineClient assemblyLineClientPasswordAuth(PasswordAuthProperties passwordAuthProperties,
                                                             ObjectMapper defaultMapper,
                                                             AssemblylineClientProperties assemblyLineClientProperties,
                                                             ProxyProperties proxyProperties) {
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication(passwordAuthProperties);
        return new AssemblylineClient(assemblyLineClientProperties, proxyProperties, defaultMapper,
                passwordAuthentication);
    }
}
