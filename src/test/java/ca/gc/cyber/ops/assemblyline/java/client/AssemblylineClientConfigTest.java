package ca.gc.cyber.ops.assemblyline.java.client;

import ca.gc.cyber.ops.assemblyline.java.client.clients.ProxyProperties;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssemblylineClientConfigTest {

    @Test
    void testHttpClientNullProxyPort() {
        ProxyProperties invalidProxyProps = new ProxyProperties();
        invalidProxyProps.setHost("abc");

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> new AssemblylineClientConfig().httpClient(invalidProxyProps));
        assertEquals("Proxy port must be set if proxy host is set.", e.getMessage());
    }

    @Test
    void testHttpClientValidProxyHostAndPort() {
        ProxyProperties proxyProperties = new ProxyProperties();
        proxyProperties.setHost("abc");
        proxyProperties.setPort(1234);

        new AssemblylineClientConfig().httpClient(proxyProperties);

        // Should not throw any exception
    }
}