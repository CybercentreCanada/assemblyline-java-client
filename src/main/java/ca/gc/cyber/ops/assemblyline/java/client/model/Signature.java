package ca.gc.cyber.ops.assemblyline.java.client.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Signature {
    /**
     * Number of times that this signature triggered the heuristic
     */
    int frequency;
    /**
     * Name of the signature that triggered the heuristic
     */
    String name;
}
