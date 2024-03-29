package ca.gc.cyber.ops.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LoginResponse {

    /**
     * Privileges of signed in user
     */
    List<Privleges> privileges;
    /**
     * Duration of authenticated session
     */
    int sessionDuration;
    /**
     * Username of signed in user
     */
    String username;

    /**
     * Roles limit of signed in user
     */
    List<String> rolesLimit;

    /**
     * User privileges from assemblyline-ui/blob/master/assemblyline_ui/api/v4/authentication.py
     */
    public enum Privleges {
        R,
        W,
        E
    }
}
