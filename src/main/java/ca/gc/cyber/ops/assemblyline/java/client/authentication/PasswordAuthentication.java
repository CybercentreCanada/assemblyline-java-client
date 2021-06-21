package ca.gc.cyber.ops.assemblyline.java.client.authentication;

import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class PasswordAuthentication implements AssemblylineAuthenticationMethod {

    @Getter
    private final MultiValueMap<String, String> authBody;

    public PasswordAuthentication(PasswordAuthProperties passwordAuthProperties){

        Assert.hasLength(passwordAuthProperties.getUsername(), "Username property cannot be null or empty for AssemblyLineClientPasswordAuth");
        Assert.hasLength(passwordAuthProperties.getPassword(), "Password property cannot be null or empty for AssemblyLineClientPasswordAuth");

        authBody = new LinkedMultiValueMap<>();
        authBody.put("user", List.of(passwordAuthProperties.getUsername()));
        authBody.put("password", List.of(passwordAuthProperties.getPassword()));
    }
}
