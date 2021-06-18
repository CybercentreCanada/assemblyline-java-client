package ca.gc.cyber.ops.assemblyline.java.client.model.ingest;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Value
public class UrlIngest extends NonBinaryIngest{

    @NotNull
    String url;

}
