package ca.gc.cyber.assemblyline.java.client.authentication;

import org.springframework.util.MultiValueMap;

public interface AssemblylineAuthenticationMethod {

    MultiValueMap<String, String> getAuthBody();
}
