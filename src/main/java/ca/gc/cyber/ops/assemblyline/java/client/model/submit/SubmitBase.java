package ca.gc.cyber.ops.assemblyline.java.client.model.submit;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder
@Data
@NoArgsConstructor
public class SubmitBase {

    /**
     * File Name
     */
    private String name;
    /**
     * Submission Metadata
     */
    private Map<String, Object> metadata;
    /**
     * Submission Parameters
     */
    private Map<String, Object> params;
    /**
     * Submission profile name
     */
    private String submissionProfile;
}
