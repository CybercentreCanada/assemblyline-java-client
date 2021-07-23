package ca.gc.cyber.ops.assemblyline.java.client.model.ingest;

import ca.gc.cyber.ops.assemblyline.java.client.model.submit.SubmitBase;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class IngestBase extends SubmitBase {

    /**
     * Generate Alert for when submitted
     */
    private Boolean generateAlert;
    /**
     * Notification Queue ingest submission responses can be read from
     */
    private String notificationQueue;
    /**
     * Notification Queue threshold
     */
    private int notificationThreshold;
}
