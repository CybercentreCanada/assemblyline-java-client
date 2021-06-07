package ca.gc.cyber.assemblyline.java.client.model.ingest;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Value
public class Sha256Ingest extends NonBinaryIngest {

    @NotNull
    String sha256;
}
