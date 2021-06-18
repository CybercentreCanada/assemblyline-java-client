package ca.gc.cyber.ops.assemblyline.java.client.model.submit;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder
@Value
public class Sha256Submit extends NonBinarySubmit{

    @NotNull
    String sha256;
}
