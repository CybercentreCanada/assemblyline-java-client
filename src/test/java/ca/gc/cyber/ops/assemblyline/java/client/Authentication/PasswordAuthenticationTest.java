package ca.gc.cyber.ops.assemblyline.java.client.Authentication;

import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthProperties;
import ca.gc.cyber.ops.assemblyline.java.client.authentication.PasswordAuthentication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordAuthenticationTest {

    @Test
    void testConstruct() {
        String user = "user";
        String password = "password";

        PasswordAuthProperties passwordAuthProperties = new PasswordAuthProperties(password, user);
        new PasswordAuthentication(passwordAuthProperties);
        PasswordAuthProperties passwordAuthPropertiesEmptyPassword = new PasswordAuthProperties("", user);
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new PasswordAuthentication(passwordAuthPropertiesEmptyPassword));
        PasswordAuthProperties passwordAuthPropertiesEmptyUsername = new PasswordAuthProperties(password, "");
        Assertions.assertThrows(IllegalArgumentException.class,
                () ->  new PasswordAuthentication(passwordAuthPropertiesEmptyUsername));

    }
}
