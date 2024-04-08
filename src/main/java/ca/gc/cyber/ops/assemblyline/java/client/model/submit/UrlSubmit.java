package ca.gc.cyber.ops.assemblyline.java.client.model.submit;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

@SuperBuilder
@Value
public class UrlSubmit extends NonBinarySubmit{

    @NotNull
    String url;
}
