package ca.gc.cyber.ops.assemblyline.java.client;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.clients.AssemblylineClientProperties;
import ca.gc.cyber.ops.assemblyline.java.client.clients.ProxyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssemblylineClientConfigTest {
    private static final ObjectMapper defaultMapper = new ObjectMapper();

    @Test
    void testNullProxyPort() {
        ApikeyAuthProperties authProperties = new ApikeyAuthProperties();
        authProperties.setUsername("username");
        authProperties.setApikey("apikey");
        AssemblylineClientProperties assemblylineClientProperties = new AssemblylineClientProperties();

        ProxyProperties invalidProxyProps = new ProxyProperties();
        invalidProxyProps.setHost("abc");

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new AssemblylineClientConfig().assemblylineClientApiAuth(authProperties, defaultMapper, assemblylineClientProperties, invalidProxyProps));
        assertEquals("Proxy port must be set if proxy host is set.", e.getMessage());
    }

    @Test
    void testValidProxyHostAndPort() {
        ApikeyAuthProperties authProperties = new ApikeyAuthProperties();
        authProperties.setUsername("username");
        authProperties.setApikey("apikey");
        AssemblylineClientProperties assemblylineClientProperties = new AssemblylineClientProperties();

        ProxyProperties proxyProperties = new ProxyProperties();
        proxyProperties.setHost("abc");
        proxyProperties.setPort(1234);

        new AssemblylineClientConfig().assemblylineClientApiAuth(authProperties, defaultMapper, assemblylineClientProperties, proxyProperties);

        // Should not throw any exception
    }
}