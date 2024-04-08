package ca.gc.cyber.ops.assemblyline.java.client.model.ingest;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

@SuperBuilder
@Value
public class UrlIngest extends NonBinaryIngest {

    @NotNull
    String url;

}
