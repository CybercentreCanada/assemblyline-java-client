package ca.gc.cyber.assemblyline.java.client.clients;

import ca.gc.cyber.assemblyline.java.client.authentication.AssemblylineAuthenticationMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class AssemblylineAuthenticationTestImpl implements AssemblylineAuthenticationMethod {

    @Override
    public MultiValueMap<String, String> getAuthBody() {
        MultiValueMap<String, String> authBody = new LinkedMultiValueMap<>();
        authBody.put("test", List.of("test"));
        return authBody;
    }

}
