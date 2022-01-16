package ca.gc.cyber.ops.assemblyline.java.client;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.AssemblylineAuthenticationMethod;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClient;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClientProperties;
import ca.gc.cyber.ops.assemblyline.java.client.clients.IAssemblylineClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(
        {ApikeyAuthProperties.class, PasswordAuthProperties.class, AssemblylineClientProperties.class})
@Slf4j
public class AssemblylineClientConfig {

    @Bean
    @ConditionalOnProperty("assemblyline-java-client.url")
    @ConditionalOnMissingBean
    public IAssemblylineClient assemblylineClient(HttpClient assemblylineHttpClient, AssemblylineAuthenticationMethod authMethod,
                                                  ObjectMapper defaultMapper,
                                                  AssemblylineClientProperties assemblylineClientProperties) {
        return new AssemblylineClient(assemblylineClientProperties, assemblylineHttpClient, defaultMapper,
                authMethod);
    }

    @Bean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "apikey")
    @ConditionalOnMissingBean
    public AssemblylineAuthenticationMethod assemblylineClientApiAuth(ApikeyAuthProperties apikeyAuthProperties) {
        return new ApikeyAuthentication(apikeyAuthProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "password")
    public AssemblylineAuthenticationMethod assemblyLineClientPasswordAuth(PasswordAuthProperties passwordAuthProperties) {
        return new PasswordAuthentication(passwordAuthProperties);
    }

    /**
     * Returns a basic HttpClient with HTTPS support enabled.
     *
     * @return HttpClient
     */
    @Bean
    @ConditionalOnMissingBean
    public HttpClient assemblylineHttpClient() {
        return HttpClient.create().secure();
    }
}
