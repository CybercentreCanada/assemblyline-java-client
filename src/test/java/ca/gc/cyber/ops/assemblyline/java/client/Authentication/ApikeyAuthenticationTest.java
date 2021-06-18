package ca.gc.cyber.ops.assemblyline.java.client.Authentication;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.ApikeyAuthentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ApikeyAuthenticationTest {

    @Test
    void testConstruct() {
        String user = "user";
        String apikey = "apikey";

        ApikeyAuthProperties apikeyAuthProperties = new ApikeyAuthProperties(apikey, user);
        new ApikeyAuthentication(apikeyAuthProperties);
        ApikeyAuthProperties apikeyAuthPropertiesEmptyApikey = new ApikeyAuthProperties("", user);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ApikeyAuthentication(apikeyAuthPropertiesEmptyApikey));
        ApikeyAuthProperties apikeyAuthPropertiesEmptyUsername = new ApikeyAuthProperties(apikey, "");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ApikeyAuthentication(apikeyAuthPropertiesEmptyUsername));

    }
}
