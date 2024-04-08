package ca.gc.cyber.ops.assemblyline.java.client.model.ingest;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

@SuperBuilder
@Value
public class Sha256Ingest extends NonBinaryIngest {

    @NotNull
    String sha256;
}
