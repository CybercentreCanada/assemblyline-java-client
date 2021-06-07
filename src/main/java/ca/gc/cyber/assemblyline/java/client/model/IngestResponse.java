package ca.gc.cyber.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IngestResponse {

    String ingestId;
}
