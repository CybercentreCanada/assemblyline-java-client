package ca.gc.cyber.ops.assemblyline.java.client;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthentication;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClient;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClientProperties;
import ca.gc.cyber.ops.assemblyline.java.client.clients.ProxyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.util.Assert.notNull;

@Configuration
@EnableConfigurationProperties(
        {ApikeyAuthProperties.class, PasswordAuthProperties.class, AssemblylineClientProperties.class,
                ProxyProperties.class})
@Slf4j
public class AssemblylineClientConfig {

    @Bean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "apikey")
    public AssemblylineClient assemblylineClientApiAuth(ApikeyAuthProperties apikeyAuthProperties,
                                                        ObjectMapper defaultMapper,
                                                        AssemblylineClientProperties assemblylineClientProperties,
                                                        ProxyProperties proxyProperties) {
        ApikeyAuthentication apikeyAuthentication = new ApikeyAuthentication(apikeyAuthProperties);
        return new AssemblylineClient(assemblylineClientProperties, createHttpClient(proxyProperties), defaultMapper,
                apikeyAuthentication);
    }

    @Bean
    @ConditionalOnProperty(name = "assemblyline-java-client.auth-method", havingValue = "password")
    public AssemblylineClient assemblyLineClientPasswordAuth(PasswordAuthProperties passwordAuthProperties,
                                                             ObjectMapper defaultMapper,
                                                             AssemblylineClientProperties assemblyLineClientProperties,
                                                             ProxyProperties proxyProperties) {
        PasswordAuthentication passwordAuthentication = new PasswordAuthentication(passwordAuthProperties);
        return new AssemblylineClient(assemblyLineClientProperties, createHttpClient(proxyProperties), defaultMapper,
                passwordAuthentication);
    }

    /**
     * Return an HttpClient that uses the proxy setup via the properties, if properties host and port are both set. If
     * no proxy properties are set, a 'secure' Httpclient is returned without proxy configured.
     *
     * @return HttpClient
     * @throws IllegalArgumentException If proxy port is not valid
     */
    private static HttpClient createHttpClient(ProxyProperties proxyProperties) {
        HttpClient httpClient = HttpClient.create().secure();

        String proxyHost = proxyProperties.getHost();
        if (proxyHost != null) {
            Integer proxyPort = proxyProperties.getPort();
            hasLength(proxyHost, "Proxy host, when set, must not be empty.");
            notNull(proxyPort, "Proxy port must be set if proxy host is set.");

            log.debug("HTTP client is configured to use the proxy {} on port {}.", proxyHost, proxyPort);
            httpClient = httpClient.proxy(proxy -> proxy
                    .type(ProxyProvider.Proxy.HTTP)
                    .host(proxyHost)
                    .port(proxyPort));
        } else {
            log.debug("No proxy host set. HTTP client will not be configured to go through a proxy.");
        }

        return httpClient;
    }
}
