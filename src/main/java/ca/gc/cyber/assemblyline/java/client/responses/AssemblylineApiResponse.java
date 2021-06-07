package ca.gc.cyber.assemblyline.java.client.responses;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AssemblylineApiResponse<T> {
    String apiErrorMessage;
    T apiResponse;
    String apiServerVersion;
    String apiStatusCode;
}
