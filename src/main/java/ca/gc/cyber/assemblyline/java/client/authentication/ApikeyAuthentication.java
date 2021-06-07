package ca.gc.cyber.assemblyline.java.client.authentication;

import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class ApikeyAuthentication implements AssemblylineAuthenticationMethod {

    @Getter
    private final MultiValueMap<String, String> authBody;

    public ApikeyAuthentication(ApikeyAuthProperties apikeyAuthProperties){
        Assert.hasLength(apikeyAuthProperties.getUsername(), "Username property cannot be null or emtpy for AssemblyLineClientApiAuth");
        Assert.hasLength(apikeyAuthProperties.getApikey(), "Apikey property cannot be null or empty for AssemblyLineClientApiAuth");

        authBody = new LinkedMultiValueMap<>();
        authBody.put("user", List.of(apikeyAuthProperties.getUsername()));
        authBody.put("apikey", List.of(apikeyAuthProperties.getApikey()));
    }
}
