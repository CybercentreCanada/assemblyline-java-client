package ca.gc.cyber.ops.assemblyline.java.client.model.submit;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Value
public class UrlSubmit extends NonBinarySubmit{

    @NotNull
    String url;
}
